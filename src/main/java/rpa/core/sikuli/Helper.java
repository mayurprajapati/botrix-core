package rpa.core.sikuli;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.sikuli.script.Match;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;

public class Helper {
	private static Logger logger = LoggerFactory.getLogger(Helper.class);

	public static List<Match> sortMatchesByPosition(List<Match> allMatches) {
		TreeMap<String, Match> sortedMatches = new TreeMap<>();
		for (Match match : allMatches) {
			sortedMatches.put("" + match.x + match.y, match);
		}

		List<Match> sortedMatchesAsList = new ArrayList<Match>(sortedMatches.values());
		return sortedMatchesAsList;
	}
}
