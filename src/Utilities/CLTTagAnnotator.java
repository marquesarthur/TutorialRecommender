package Utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class CLTTagAnnotator implements Annotator {

	public static 	LexicalizedParser lLexicalizedParser = LexicalizedParser
			.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
	public CLTTagAnnotator() {
		// TODO Auto-generated constructor stub
	}
	
	public CLTTagAnnotator(String annotatorName, Properties props) {
	}


	@Override
	public void annotate(Annotation annotation) {
		
	    for (CoreMap sentence: annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
	    	
	        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
	        
	        List<TaggedWord> tagged = getPOSTags(tokens);
	        
	        for (int i = 0; i < tokens.size(); ++i) {
	            tokens.get(i).set(PartOfSpeechAnnotation.class, tagged.get(i).tag());
	        }
	    }

	}
	
	// assums working for one sentence.
	private static List<TaggedWord> getPOSTags(List<CoreLabel> tokens)
	{
		List<List<TaggedWord>> lResult = new ArrayList<List<TaggedWord>>();
	
		for (CoreLabel token:tokens)
		{
			String word = token.get(TextAnnotation.class).toLowerCase();
			if (word.startsWith("clt_") || word.startsWith("codeid"))
				token.setTag("NN");
		}
		
		Tree lParse = lLexicalizedParser.apply(tokens);
		
		lResult.add(lParse.taggedYield());
		
		return lResult.get(0);
	}
	
}
