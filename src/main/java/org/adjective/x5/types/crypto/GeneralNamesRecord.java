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
                final X5Value<?> value;
                switch (name.getTagNo()) {
                    case GeneralName.rfc822Name:
                    case GeneralName.dNSName:
                    case GeneralName.uniformResourceIdentifier:
                        value = Values.string(DERIA5String.getInstance(name.getName()), source);
                        break;

                    case GeneralName.iPAddress:
                        value = Values.ipAddress(DEROctetString.getInstance(name.getName()), source);
                        break;
                    case GeneralName.directoryName:
                        value = dnOrErrorValue(X500Name.getInstance(name.getName()), source);
                        break;
                    case GeneralName.x400Address:
                    case GeneralName.registeredID:
                    case GeneralName.ediPartyName:
                        value = Values.asn1(name.getName().toASN1Primitive(), source);
                        break;
                    default:
                        throw new UnsupportedOperationException("Cannot handle name tag: " + name.getTagNo());
                }
                ;
                populate(names, getKeyForTag(name.getTagNo()), value);
            }
        }

        var fields = new LinkedHashMap<String, ValueSequence>();
        names.forEach((key, list) -> fields.put(key, new ValueSequence(list, source)));
        return fields;
    }

    private static X5Value<?> dnOrErrorValue(X500Name x500Name, X5StreamInfo source) {
        try {
            return Values.dn(x500Name, source);
        } catch (DnParseException e) {
            return Values.error(e, source);
        }
    }

    private static void populate(Map<String, List<X5Value>> map, String key, X5Value value) {
        map.computeIfAbsent(key, ignore -> new ArrayList<>()).add(value);
    }

    private static String getKeyForTag(int tagNo) {
        switch (tagNo) {
            case 1:
                return "rfc822";
            case 2:
                return "dns";
            case 3:
                return "x400";
            case 4:
                return "x500";
            case 5:
                return "edi";
            case 6:
                return "uri";
            case 7:
                return "ip";
            case 8:
                return "id";
            default:
                return "tag:" + tagNo;
        }
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
