import React, { createContext, useContext, useEffect, useState } from "react";

const SearchContext = createContext();

export const SearchProvider = ({ children }) => {
  const [recentSearchList, setRecentSearchList] = useState(
    () => JSON.parse(localStorage.getItem("recentSearchList")) || []
  );

  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);

  const [items, setItems] = useState([]);
  const [time, setTime] = useState(0);

  const addSearchItem = (searchValue) => {
    if (recentSearchList.includes(searchValue)) {
      return;
    }
    const updatedList = [...recentSearchList, searchValue];
    setRecentSearchList(updatedList);
  };

  const removeRecentSearchItem = (index) => {
    const updatedList = recentSearchList.filter((_, i) => i !== index);
    setRecentSearchList(updatedList);
  };

  useEffect(() => {
    localStorage.setItem("recentSearchList", JSON.stringify(recentSearchList));
  }, [recentSearchList]);

  return (
    <SearchContext.Provider
      value={{
        recentSearchList,
        addSearchItem,
        removeRecentSearchItem,
        setSuggestions,
        suggestions,
        setQuery,
        query,
        items,
        setItems,
        time,
        setTime,
      }}
    >
      {children}
    </SearchContext.Provider>
  );
};

export const useSearch = () => {
  return useContext(SearchContext);
};
