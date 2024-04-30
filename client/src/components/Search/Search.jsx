import React, { useState } from "react";
import SearchCSS from "./Search.module.css";
import Searchbar from "../Searchbar/Searchbar";

const RecentSearchItem = ({ item, index, onDelete, onRemove }) => {
  return (
    <div
      className={`${SearchCSS.flex_row} ${SearchCSS.recent_search__card} montserrat-medium`}
    >
      {item}
      <span className={`${SearchCSS.icon}`} onClick={() => onRemove(index)}>
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512">
          {/*}!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.*/}
          <path d="M342.6 150.6c12.5-12.5 12.5-32.8 0-45.3s-32.8-12.5-45.3 0L192 210.7 86.6 105.4c-12.5-12.5-32.8-12.5-45.3 0s-12.5 32.8 0 45.3L146.7 256 41.4 361.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L192 301.3 297.4 406.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3L237.3 256 342.6 150.6z" />
        </svg>
      </span>
    </div>
  );
};

const RecentSearch = () => {
  const [recentSearchList, setRecentSearchList] = useState([
    "Football",
    "Coursera",
    "Music",
  ]);

  const removeRecentSearchItem = (index) => {
    const searchList = [...recentSearchList];
    searchList.splice(index, 1);
    setRecentSearchList(searchList);
  };

  return (
    <div className={`${SearchCSS.flex_row} ${SearchCSS.recent_search}`}>
      <div className={`${SearchCSS.flex_row} ${SearchCSS.recent_search__text}`}>
        <div className={`${SearchCSS.icon}`}>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            {/*!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.*/}
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
            <RecentSearchItem
              item={item}
              key={i + "_" + item}
              index={i}
              onRemove={removeRecentSearchItem}
            />
          );
        })}
      </div>
    </div>
  );
};

const Search = ({ className }) => {
  return (
    <div className={`${SearchCSS.search_body} ${className}`}>
      <Searchbar />
      <div className={`${SearchCSS.separator}`}>
        <span className={`${SearchCSS.separator__info} montserrat-light`}>
          Quick Search
        </span>
        <span className={`${SearchCSS.separator__line}`}></span>
      </div>
      <div className={`${SearchCSS.section} ${SearchCSS.flex_row}`}>
        <RecentSearch />
        <a className={`${SearchCSS.flex_row} ${SearchCSS.game_btn}`}>
          <div className={`${SearchCSS.icon}`}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
              {/*!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.*/}
              <path d="M158.4 32.6c4.8-12.4-1.4-26.3-13.8-31s-26.3 1.4-31 13.8L81.1 100c-7.9 20.7-3 44.1 12.7 59.7l57.4 57.4L70.8 190.3c-2.4-.8-4.3-2.7-5.1-5.1L46.8 128.4C42.6 115.8 29 109 16.4 113.2S-3 131 1.2 143.6l18.9 56.8c5.6 16.7 18.7 29.8 35.4 35.4L116.1 256 55.6 276.2c-16.7 5.6-29.8 18.7-35.4 35.4L1.2 368.4C-3 381 3.8 394.6 16.4 398.8s26.2-2.6 30.4-15.2l18.9-56.8c.8-2.4 2.7-4.3 5.1-5.1l80.4-26.8L93.7 352.3C78.1 368 73.1 391.4 81.1 412l32.5 84.6c4.8 12.4 18.6 18.5 31 13.8s18.5-18.6 13.8-31l-32.5-84.6c-1.1-3-.4-6.3 1.8-8.5L160 353.9c1 52.1 43.6 94.1 96 94.1s95-41.9 96-94.1l32.3 32.3c2.2 2.2 2.9 5.6 1.8 8.5l-32.5 84.6c-4.8 12.4 1.4 26.3 13.8 31s26.3-1.4 31-13.8L430.9 412c7.9-20.7 3-44.1-12.7-59.7l-57.4-57.4 80.4 26.8c2.4 .8 4.3 2.7 5.1 5.1l18.9 56.8c4.2 12.6 17.8 19.4 30.4 15.2s19.4-17.8 15.2-30.4l-18.9-56.8c-5.6-16.7-18.7-29.8-35.4-35.4L395.9 256l60.5-20.2c16.7-5.6 29.8-18.7 35.4-35.4l18.9-56.8c4.2-12.6-2.6-26.2-15.2-30.4s-26.2 2.6-30.4 15.2l-18.9 56.8c-.8 2.4-2.7 4.3-5.1 5.1l-80.4 26.8 57.4-57.4c15.6-15.6 20.6-39 12.7-59.7L398.4 15.4C393.6 3 379.8-3.2 367.4 1.6s-18.5 18.6-13.8 31l32.5 84.6c1.1 3 .4 6.3-1.8 8.5L336 174.1V160c0-31.8-18.6-59.3-45.5-72.2c-9.1-4.4-18.5 3.3-18.5 13.4V112c0 8.8-7.2 16-16 16s-16-7.2-16-16V101.2c0-10.1-9.4-17.7-18.5-13.4C194.6 100.7 176 128.2 176 160v14.1l-48.3-48.3c-2.2-2.2-2.9-5.6-1.8-8.5l32.5-84.6z" />
            </svg>
          </div>
          <h4 className={`${SearchCSS.icon__text} montserrat-semibold`}>
            Play Game
          </h4>
        </a>
      </div>
    </div>
  );
};

export default Search;
