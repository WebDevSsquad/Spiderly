import React, { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useSearch } from "../../utils/SearchContext";
import querySuggester from "../../utils/querySuggester";
import SearchbarCSS from "./Searchbar.module.css";
const Searchbar = ({ className, handleSearch, time, show }) => {
  const inputRef = useRef(null);
  const ghostRef = useRef(null);
  const caretRef = useRef(null);
  const [openSuggestions, setOpenSuggestions] = React.useState(false);
  const { suggestions, query, setQuery, setSuggestions } = useSearch();

  let inputElement, fakeCaret, fakeCaretInputGhost;

  useEffect(() => {
    // Add event listener for input change
    inputElement = inputRef.current;
    fakeCaretInputGhost = ghostRef.current;
    fakeCaret = caretRef.current;
    const eventTypes = [
      "input",
      "keydown",
      "keyup",
      "propertychange",
      "click",
      "paste",
      "cut",
      "copy",
      "mousedown",
      "mouseup",
      "change",
    ];

    eventTypes.forEach((eventType) => {
      inputElement.addEventListener(eventType, moveCaret);
    });

    // Cleanup: remove event listener on component unmount
    return () => {
      eventTypes.forEach((eventType) => {
        inputElement.removeEventListener(eventType, moveCaret);
      });
    };
  }, []);

  function setCaretXY() {
    const rects = fakeCaretInputGhost.getClientRects();
    const lastRect = rects[rects.length - 1];
    const realElementOffset = inputElement.getBoundingClientRect();

    let x =
      lastRect.left + lastRect.width - realElementOffset.left + window.scrollX;
    if (x > realElementOffset.width) x = realElementOffset.width;
    fakeCaret.style.left = `${x}px`;
  }

  const moveCaret = () => {
    updateSuggestions(inputElement.value);

    fakeCaretInputGhost.textContent = inputElement.value
      .substring(0, inputElement.selectionStart)
      .replace(/\n$/, "\n\u0001");
    setCaretXY();
  };

  const updateSuggestions = (query) => {
    const querySuggestions = querySuggester.search(query);
    console.log(querySuggestions);
    setSuggestions(querySuggestions);
    setQuery(query);
  };

  const navigate = useNavigate();

  const search = () => {
    setSuggestions([]);
    console.log(inputRef.current.value);
    handleSearch(inputRef.current.value, navigate);
  };

  const myRef = useRef();

  useEffect(() => {
    function handleClickOutside(event) {
      if (myRef.current && !myRef.current.contains(event.target)) {
        // Hide your element here
        setSuggestions([]);
      }
    }

    // Bind the event listener
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      // Unbind the event listener on clean up
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [myRef]);

  return (
    <>
      <div className={`${SearchbarCSS.searchbar} ${className}`}>
        <div className={`${SearchbarCSS.searchbar_icon}`} onClick={search}>
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M416 208c0 45.9-14.9 88.3-40 122.7L502.6 457.4c12.5 12.5 12.5 32.8 0 45.3s-32.8 12.5-45.3 0L330.7 376c-34.4 25.2-76.8 40-122.7 40C93.1 416 0 322.9 0 208S93.1 0 208 0S416 93.1 416 208zM208 352a144 144 0 1 0 0-288 144 144 0 1 0 0 288z" />
          </svg>
        </div>
        <div className={`${SearchbarCSS.searchbar_input_section}`}>
          <div className={`${SearchbarCSS.searchbar_input__caret_container}`}>
            <span
              id="search-input__ghost"
              ref={ghostRef}
              className={`${SearchbarCSS.searchbar_input__ghost}`}
            ></span>
          </div>
          <div
            ref={caretRef}
            className={`${SearchbarCSS.searchbar_input__caret}`}
          ></div>
          <input
            ref={inputRef}
            id="searchbar_input"
            type="text"
            className={`${SearchbarCSS.searchbar_input}`}
            placeholder="Search by Name, Mood, Color, Time, etc ..."
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                search();
              }
            }}
          />
        </div>
        <div className={`${SearchbarCSS.time}`}>
          {time}sec
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
            <path d="M256 0a256 256 0 1 1 0 512A256 256 0 1 1 256 0zM232 120V256c0 8 4 15.5 10.7 20l96 64c11 7.4 25.9 4.4 33.3-6.7s4.4-25.9-6.7-33.3L280 243.2V120c0-13.3-10.7-24-24-24s-24 10.7-24 24z" />
          </svg>
        </div>
      </div>
      {suggestions.length > 0 && query.length > 0 && show && (
        <div ref={myRef} className={`${SearchbarCSS.suggestion}`}>
          {suggestions.map((suggestion) => (
            <span
              onClick={(e) => {
                inputRef.current.value = e.target.innerText;
                search();
              }}
              className={`${SearchbarCSS.suggestion_item}`}
              key={suggestion}
            >
              {suggestion}
            </span>
          ))}
        </div>
      )}
    </>
  );
};

export default Searchbar;
