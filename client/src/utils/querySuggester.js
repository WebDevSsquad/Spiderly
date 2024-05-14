import Trie from "./Trie";
import axios from "axios";

const querySuggester = (() => {
  const trie = new Trie();

  const fetchSuggestions = async () => {
    let list = [];
    try {
      const url = `http://localhost:8080/suggestions`;
      const response = await axios.get(url, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      list = response.data.suggestions;
      console.log(list);
    } catch (error) {
      console.error("Error fetching data:", error);
      // Handle the error appropriately
    }
    list.forEach(item => trie.insert(item.toLowerCase()));
    // const res = await response.json();
  };

  fetchSuggestions();

  const search = (query) => {
    const suggestions = trie.suggestions(query.toLowerCase()).slice(0, 6);

    return suggestions;
  }

  return {
    search,
  }

})();

export default querySuggester;