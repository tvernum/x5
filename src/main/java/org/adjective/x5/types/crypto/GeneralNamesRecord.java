package org.adjective.x5.types.crypto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.adjective.x5.exception.DnParseException;
import org.adjective.x5.types.ValueSequence;
import org.adjective.x5.types.X5Object;
import org.adjective.x5.types.X5Record;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Value;
import org.adjective.x5.types.value.OID;
import org.adjective.x5.util.ObjectIdentifiers;
import org.adjective.x5.util.Values;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class GeneralNamesRecord implements X5Record {

    private final GeneralNames names;
    private final X5StreamInfo source;
    private final Map<String, ValueSequence> fields;

    public GeneralNamesRecord(GeneralNames names, X5StreamInfo source) {
        this.names = names;
        this.source = source;
        this.fields = buildFields(names, source);
    }

    @Override
    public String description() {
        if (fields.isEmpty()) {
            return "";
        }
        var str = new StringBuilder();
        fields.forEach((key, seq) -> {
            str.append(key).append("=").append(seq.description());
            str.append(' ');
        });
        str.setLength(str.length() - 1);
        return str.toString();
    }

    private static Map<String, ValueSequence> buildFields(GeneralNames generalNames, X5StreamInfo source) {
        if (generalNames == null) {
            return Map.of();
        }

        final Map<String, List<X5Value>> names = new LinkedHashMap<>();
        for (GeneralName name : generalNames.getNames()) {
            if (name.getTagNo() == GeneralName.otherName) {
                // Need special handling here
                final ASN1Sequence seq = DERSequence.getInstance(name.getName());
                final OID oid = new OID(ASN1ObjectIdentifier.getInstance(seq.getObjectAt(0)), source);
                final String key = "other:" + ObjectIdentifiers.friendlyName(oid).orElse(oid.toString());
                populate(names, key, Values.asn1(seq.getObjectAt(1), source));
            } else {
                X5Value<?> value = switch (name.getTagNo()) {
                    case GeneralName.rfc822Name, GeneralName.dNSName, GeneralName.uniformResourceIdentifier -> Values.string(
                        DERIA5String.getInstance(name.getName()),
                        source
                    );

                    case GeneralName.iPAddress -> Values.ipAddress(DEROctetString.getInstance(name.getName()), source);

                    case GeneralName.directoryName -> {
                        try {
                            yield Values.dn(X500Name.getInstance(name.getName()), source);
                        } catch (DnParseException e) {
                            yield Values.error(e, source);
                        }
                    }

                    case GeneralName.x400Address, GeneralName.registeredID, GeneralName.ediPartyName -> Values.asn1(
                        name.getName().toASN1Primitive(),
                        source
                    );
                    default -> throw new UnsupportedOperationException("Cannot handle name tag: " + name.getTagNo());
                };
                populate(names, getKeyForTag(name.getTagNo()), value);
            }
        }

        var fields = new LinkedHashMap<String, ValueSequence>();
        names.forEach((key, list) -> fields.put(key, new ValueSequence(list, source)));
        return fields;
    }

    private static void populate(Map<String, List<X5Value>> map, String key, X5Value value) {
        map.computeIfAbsent(key, ignore -> new ArrayList<>()).add(value);
    }

    private static String getKeyForTag(int tagNo) {
        return switch (tagNo) {
            case 1 -> "rfc822";
            case 2 -> "dns";
            case 3 -> "x400";
            case 4 -> "x500";
            case 5 -> "edi";
            case 6 -> "uri";
            case 7 -> "ip";
            case 8 -> "id";
            default -> "tag:" + tagNo;
        };
    }

    @Override
    public X5StreamInfo getSource() {
        return source;
    }

    @Override
    public Map<String, ? extends X5Object> asMap() {
        return fields;
    }

    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public Set<String> names() {
        return fields.keySet();
    }

    @Override
    public X5Object value(String name) {
        return fields.get(name);
    }
}
