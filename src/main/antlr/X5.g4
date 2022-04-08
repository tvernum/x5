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
grammar X5;

@header {
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
package org.adjective.x5.cli.parser;
}

cli : exprList EOF ;

exprList: expr (';' expr)* ;

expr  : exprElement ('|' exprElement)* ;

exprElement
  : operatorExpr
  | commandExpr
  | functionExpr
  | blockExpr
  | subExpr
  | literalExpr
  ;

operatorExpr: operator commandArgs ;
operator: DOT | EQ | NEQ | QUEST | COLON;

commandExpr: commandName commandArgs ;
commandName: Word;
commandArgs:  ( Word | QuotedWord )*;

functionExpr: Word '(' functionArgs? ')';
functionArgs: expr ( ',' expr )*;

blockExpr: Word '{' exprList '}';
subExpr: '(' exprList ')';

literalExpr: QuotedWord | Number;

PAREN_L: '(';
PAREN_R: ')';
BRACE_L: '{';
BRACE_R: '}';
COMMA: ',';
PIPE: '|';
SEMI: ';';

DOT : '.';
EQ: '=';
NEQ: '!=';
QUEST: '?';
COLON: ':';

QuotedWord
  : DOUBLE_QUOTE ( EscapedChar | SafeCharDQ)* DOUBLE_QUOTE
  | SINGLE_QUOTE (SafeCharSQ)* SINGLE_QUOTE
  ;

DOUBLE_QUOTE: '"';
SINGLE_QUOTE: '\'';

Number
   : [+\-]? [0-9]+ ('.' [0-9]+)?
   ;

fragment SafeCharDQ: ~ ["\\\u0000-\u001F];
fragment SafeCharSQ: ~ ['\\\u0000-\u001F];
fragment EscapedChar: '\\' (["\\bfnrt] | UnicodeLiteral);
fragment UnicodeLiteral: 'u' Hexchar Hexchar Hexchar Hexchar;
fragment Hexchar: [0-9a-fA-F];

WS: [ \r\n\t] + -> skip
  ;

Word: WordStart (WordOther)+ ;

fragment WordStart: [a-zA-Z0-9/\-#];
fragment WordOther: ~ [(){},|; \r\n\t];
