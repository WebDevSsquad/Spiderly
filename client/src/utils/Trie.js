class TrieNode {
  constructor() {
      this.children = new Map();
      this.isEndOfWord = false;
  }
}

class Trie {
  constructor() {
      this.root = new TrieNode();
  }

  insert(word) {
      let current = this.root;
      for (let char of word) {
          if (!current.children.has(char)) {
              current.children.set(char, new TrieNode());
          }
          current = current.children.get(char);
      }
      current.isEndOfWord = true;
  }

  search(word) {
      let current = this.root;
      for (let char of word) {
          if (!current.children.has(char)) {
              return false;
          }
          current = current.children.get(char);
      }
      return current.isEndOfWord;
  }

  startsWith(prefix) {
      let current = this.root;
      for (let char of prefix) {
          if (!current.children.has(char)) {
              return false;
          }
          current = current.children.get(char);
      }
      return true;
  }

  suggestions(prefix) {
      let current = this.root;
      for (let char of prefix) {
          if (!current.children.has(char)) {
              return [];
          }
          current = current.children.get(char);
      }
      return this._dfs(current, prefix);
  }

  _dfs(node, prefix) {
      let suggestions = [];
      if (node.isEndOfWord) {
          suggestions.push(prefix);
      }
      for (let [char, child] of node.children.entries()) {
          suggestions.push(...this._dfs(child, prefix + char));
      }
      return suggestions;
  }
}

export default Trie;