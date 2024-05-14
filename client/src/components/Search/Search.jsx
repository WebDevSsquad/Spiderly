import React, { useState } from "react";
import { SearchProvider, useSearch } from "../../utils/SearchContext";
import Searchbar from "../Searchbar/Searchbar";
import SearchCSS from "./Search.module.css";

const RecentSearchItem = ({ item, index }) => {
  const { removeRecentSearchItem } = useSearch();

  return (
    <div
      className={`${SearchCSS.flex_row} ${SearchCSS.recent_search__card} montserrat-medium`}
    >
      {item}
      <span
        className={`${SearchCSS.icon}`}
        onClick={() => removeRecentSearchItem(index)}
      >
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512">
          <path d="M342.6 150.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L192 210.7 86.6 105.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L146.7 256 41.4 361.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L192 301.3 297.4 406.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L237.3 256 342.6 150.6z" />
        </svg>
      </span>
    </div>
  );
};

const RecentSearch = () => {
  const { recentSearchList } = useSearch();

  return (
    <div className={`${SearchCSS.flex_row} ${SearchCSS.recent_search}`}>
      <div className={`${SearchCSS.flex_row} ${SearchCSS.recent_search__text}`}>
        <div className={`${SearchCSS.icon}`}>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M75 75L41 41C25.9 25.9 0 36.6 0 57.9V168c0 13.3 10.7 24 24 24H134.1c21.4 0 32.1-25.9 17-41l-30.8-30.8C155 85.5 203 64 256 64c106 0 192 86 192 192s-86 192-192 192c-40.8 0-78.6-12.7-109.7-34.4c-14.5-10.1-34.4-6.6-44.6 7.9s-6.6 34.4 7.9 44.6C151.2 495 201.7 512 256 512c141.4 0 256-114.6 256-256S397.4 0 256 0C185.3 0 121.3 28.7 75 75zm181 53c-13.3 0-24 10.7-24 24V256c0 6.4 2.5 12.5 7 17l72 72c9.4 9.4 24.6 9.4 33.9 0s9.4-24.6 0-33.9l-65-65V152c0-13.3-10.7-24-24-24z" />
          </svg>
        </div>
        <h4 className={`${SearchCSS.icon__text} montserrat-semibold`}>
          Recent Search:
        </h4>
      </div>
      <div
        className={`${SearchCSS.flex_row} ${SearchCSS.recent_search__cards}`}
      >
        {recentSearchList.map((item, i) => {
          return (
            <RecentSearchItem item={item} key={i + "_" + item} index={i} />
          );
        })}
      </div>
    </div>
  );
};

const Search = ({ handleSearch, className }) => {
  const { addSearchItem } = useSearch();

  const handleSearchSystem = (value, navigate) => {
    addSearchItem(value);
    handleSearch(value, navigate);
  };

  return (
    <div className={`${SearchCSS.search_body} ${className}`}>
      <Searchbar handleSearch={handleSearchSystem} />
      <div className={`${SearchCSS.separator}`}>
        <span className={`${SearchCSS.separator__info} montserrat-light`}>
          Quick Search
        </span>
        <span className={`${SearchCSS.separator__line}`}></span>
      </div>
      <div className={`${SearchCSS.section} ${SearchCSS.flex_row}`}>
        <RecentSearch />
      </div>
    </div>
  );
};

export default Search;
