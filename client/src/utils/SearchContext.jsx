import React, { createContext, useContext, useEffect, useState } from "react";

const SearchContext = createContext();

export const SearchProvider = ({ children }) => {
  const [recentSearchList, setRecentSearchList] = useState(
    () => JSON.parse(localStorage.getItem("recentSearchList")) || []
  );

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
    <SearchContext.Provider value={{ recentSearchList, addSearchItem, removeRecentSearchItem }}>
      {children}
    </SearchContext.Provider>
  );
};

export const useSearch = () => {
  return useContext(SearchContext);
};