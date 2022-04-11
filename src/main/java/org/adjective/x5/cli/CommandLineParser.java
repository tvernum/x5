/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adjective.x5.cli;

import static java.text.CharacterIterator.DONE;

import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.adjective.x5.command.CommandLineFunction;
import org.adjective.x5.command.Commands;
import org.adjective.x5.types.X5StreamInfo;
import org.adjective.x5.types.X5Value;
import org.adjective.x5.util.Values;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.adjective.x5.cli.parser.X5BaseVisitor;
import org.adjective.x5.cli.parser.X5Lexer;
import org.adjective.x5.cli.parser.X5Parser;

public class CommandLineParser {

    private static final X5StreamInfo SOURCE_INFO = Values.source("command-line");

    private final Commands commands;

    public CommandLineParser(Commands commands) {
        this.commands = commands;
    }

    public CommandLine parse(List<String> values) {
        return parse(String.join(" ", values));
    }

    public CommandLine parse(String cli) {
        final X5Lexer lexer = new X5Lexer(CharStreams.fromString(cli));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        X5Parser parser = new X5Parser(tokens);
        X5Parser.CliContext tree = parser.cli();
        return buildCommandLine(tree);
    }

    private CommandLine buildCommandLine(X5Parser.CliContext tree) {
        CommandLineBuilder builder = new CommandLineBuilder();
        return tree.accept(builder);
    }

    private class CommandLineBuilder extends X5BaseVisitor<CommandLine> {
        @Override
        public CommandLine visitCli(X5Parser.CliContext ctx) {
            return ctx.exprList().accept(this);
        }

        @Override
        public CommandLine visitExprList(X5Parser.ExprListContext ctx) {
            if (ctx.getChildCount() == 1) {
                return ctx.expr(0).accept(this);
            } else {
                return new MultipleCommandLine(ctx.expr().stream().map(e -> e.accept(this)).collect(Collectors.toList()));
            }
        }

        @Override
        public CommandLine visitExpr(X5Parser.ExprContext ctx) {
            final List<X5Parser.ExprElementContext> elements = ctx.exprElement();
            if (elements.size() == 1) {
                return elements.get(0).accept(this);
            }
            return new PipedCommand(elements.stream().map(e -> e.accept(this)).collect(Collectors.toList()));
        }

        @Override
        public CommandLine visitOperatorExpr(X5Parser.OperatorExprContext ctx) {
            final List<String> args = parseArguments(ctx.commandArgs());
            final Token operator = ctx.operator().getChild(TerminalNode.class, 0).getSymbol();
            switch (operator.getType()) {
                case X5Parser.DOT:
                    return new CommandExecution("property", args);
                case X5Parser.EQ:
                    return new CommandExecution("equals", args);
                case X5Parser.NEQ:
                    return new CommandExecution("not-equals", args);
                case X5Parser.QUEST:
                case X5Parser.COLON:
                default:
                    throw unexpectedToken(operator);
            }
        }

        @Override
        public CommandLine visitCommandExpr(X5Parser.CommandExprContext ctx) {
            final String name = ctx.commandName().getText();
            final List<String> args = parseArguments(ctx.commandArgs());
            return new CommandExecution(name, args);
        }

        @Override
        public CommandLine visitFunctionExpr(X5Parser.FunctionExprContext ctx) {
            final String name = ctx.Word().getText();
            final CommandLineFunction function = commands.getFunction(name)
                .orElseThrow(() -> new IllegalArgumentException("No such function '" + name + "'"));
            final List<CommandLine> args = new ArrayList<>(ctx.functionArgs().getChildCount());
            ctx.functionArgs().expr().stream().forEachOrdered(e -> {
                final CommandLine expr = e.accept(this);
                if (expr == null) {
                    throw new IllegalStateException("Failed to parse expression " + describe(e));
                }
                args.add(expr);
            });
            return new FunctionExecution(function, args);
        }

        @Override
        public CommandLine visitBlockExpr(X5Parser.BlockExprContext ctx) {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override
        public CommandLine visitSubExpr(X5Parser.SubExprContext ctx) {
            final CommandLine result = ctx.exprList().accept(this);
            return result;
        }

        @Override
        public CommandLine visitLiteralExpr(X5Parser.LiteralExprContext ctx) {
            final X5Value value = toValue(ctx);
            return new LiteralValue(value);
        }

        private X5Value toValue(X5Parser.LiteralExprContext ctx) {
            var token = ctx.getChild(TerminalNode.class, 0).getSymbol();
            switch (token.getType()) {
                case X5Parser.QuotedWord:
                    return Values.string(parseQuotedWord(token.getText()), SOURCE_INFO);
                case X5Parser.Number:
                    return Values.number(new BigDecimal(token.getText()), SOURCE_INFO);
                default:
                    throw unexpectedToken(token);
            }
        }

        private List<String> parseArguments(X5Parser.CommandArgsContext args) {
            if (args.getChildCount() == 0) {
                return List.of();
            }
            return args.children.stream().map(c -> {
                TerminalNode n = (TerminalNode) c;
                return n.getSymbol();
            }).map(s -> {
                switch (s.getType()) {
                    case X5Parser.Word:
                    case X5Parser.Number:
                        return s.getText();
                    case X5Parser.QuotedWord:
                        return parseQuotedWord(s.getText());
                    default:
                        throw unexpectedToken(s);
                }
            }).collect(Collectors.toList());
        }

        private IllegalArgumentException unexpectedToken(Token s) {
            return new IllegalArgumentException(
                "Unexpected token type #"
                    + s.getType()
                    + " ("
                    + X5Parser.VOCABULARY.getDisplayName(s.getType())
                    + ")"
                    + "["
                    + s.getText()
                    + "]"
            );
        }

        private String parseQuotedWord(String text) {
            if (text.charAt(0) == '\'') {
                if (text.length() <= 2) {
                    return "";
                } else {
                    return text.substring(1, text.length() - 1);
                }
            }
            final StringBuilder str = new StringBuilder();
            final CharacterIterator itr = new StringCharacterIterator(text, 1, text.length() - 1, 1);
            do {
                if (itr.current() == '\\') {
                    itr.next();
                    if (itr.current() == DONE) {
                        throw new IllegalArgumentException("Invalid quoted string '" + text + "'");
                    }
                    final char c = parseEscapedCharacter(itr);
                    if (c == DONE) {
                        throw new IllegalArgumentException("Invalid escape character '" + itr.current() + "' in '" + text + "'");
                    }
                    str.append(c);
                } else {
                    str.append(itr.current());
                }
            } while (itr.next() != DONE);
            return str.toString();
        }

        private char parseEscapedCharacter(CharacterIterator itr) {
            switch (itr.current()) {
                case '"':
                case '\\':
                    return itr.current();
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                case 'u':
                    StringBuilder s = new StringBuilder();
                    s.append(itr.next());
                    s.append(itr.next());
                    s.append(itr.next());
                    if (itr.current() == DONE) {
                        return DONE;
                    }
                    return (char) Integer.parseInt(s.toString(), 16);
                default:
                    return DONE;
            }
        }

    }

    private String describe(RuleNode node) {
        final String ruleName = getRuleName(node);
        return ruleName + "[" + node.getText() + "]";
    }

    private String getRuleName(RuleNode node) {
        final int index = node.getRuleContext().getRuleIndex();
        if (index >= 0 && index < X5Parser.ruleNames.length) {
            return X5Parser.ruleNames[index];
        } else {
            return node.getRuleContext().getClass().getSimpleName();
        }
    }

}
