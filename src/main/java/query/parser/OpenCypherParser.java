package query.parser;

import common.Logger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import query.parser.generated.CypherLexer;
import query.parser.generated.CypherParser;

public final class OpenCypherParser {

  private static Logger logger = Logger.getLogger();

  public OpenCypherParser() {
  }

  public CypherParser.OC_CypherContext parse(String openCypherStatement) {
    try {

      CypherLexer lexer = new CypherLexer(CharStreams.fromString(openCypherStatement));
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      CypherParser parser = new CypherParser(tokenStream);

      return parser.oC_Cypher();

    } catch (Exception e) {
      logger.error("Failed to parse openCypher statement.");
      return null;
    }
  }
}
