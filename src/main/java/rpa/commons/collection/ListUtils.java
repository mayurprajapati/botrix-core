package rpa.commons.collection;

import java.util.List;
import java.util.Optional;

public class ListUtils {
	public static <T> List<T> subListCount(List<T> list, int start, int count) {
		return subList(list, start, start + count);
	}

	public static <T> List<T> subList(List<T> list, int start, int end) {
		if (end < start || start > list.size())
			return List.of();

		return list.subList(start, Math.min(end, list.size()));
	}

	public static <T> Optional<T> getLast(List<T> list) {
		return Optional.ofNullable(list.isEmpty() ? null : list.get(list.size() - 1));
	}

	public static <T> Optional<T> getOrDefault(List<T> list, int index, T def) {
		return Optional.ofNullable(list.size() < index ? list.get(index) : def);
	}

	public static <T> Optional<T> getOrNull(List<T> list, int index) {
		return getOrDefault(list, index, null);
	}

	public static void main(String[] args) {
		List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.out.println(ListUtils.subListCount(list, 0, 1));
	}
}
