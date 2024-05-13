import Trie from "./Trie";

const querySuggester = (() => {
  const list = ["JavaScript", "Java Programming", "Javelin", "Java Protecting"];

  const trie = new Trie();

  list.forEach(item => trie.insert(item.toLowerCase()));

  const search = (query) => {
    const suggestions = trie.suggestions(query.toLowerCase()).slice(0, 6);

    return suggestions;
  }

  return {
    search,
  }

})();

export default querySuggester;