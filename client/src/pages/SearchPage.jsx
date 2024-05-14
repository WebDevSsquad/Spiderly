import axios from "axios";
import { React, useEffect, useRef, useState } from "react";
import ReactDOM from "react-dom";
import ReactPaginate from "react-paginate";
import GooglePlusIcon from "../assets/.testing/GooglePlusIcon.png";
import PinterestIcon from "../assets/.testing/PinterestIcon.png";
import YTIcon from "../assets/.testing/YoutubeIcon.png";
import { ReactComponent as ArrowLeft } from "../assets/angle-left-solid.svg";
import { ReactComponent as ArrowRight } from "../assets/angle-right-solid.svg";
import Header from "../components/Header/Header";
import SearchResult from "../components/SearchResult/SearchResult";
import Searchbar from "../components/Searchbar/Searchbar";
import { SearchProvider, useSearch } from "../utils/SearchContext";
import "./SearchPage.css";
import { searchQuery } from "../services/searchService";

const CreateSearchResult = (items, setSelectedIndex, selectedIndex) => {
  console.log(selectedIndex);
  if (!items) return null;
  return items.map((item, i) => {
    return (
      <SearchResult
        link={item.url}
        title={item.title}
        description={item.description}
        words={item.words}
        index={i}
        animate={i !== selectedIndex}
        setSelectedIndex={setSelectedIndex}
      />
    );
  });
};

const SearchPage = ({ itemsPerPage }) => {
  const {items, setItems} = useSearch();
  const {time, setTime} = useSearch();
  const handleSearch = async (query) => {
    const startTime = performance.now(); // Get end time
    const data = await searchQuery(query);
    const endTime = performance.now(); // Get end time
    const timeInSeconds = (endTime - startTime) / 1000; // Calculate time taken in seconds
    setTime(timeInSeconds.toFixed(2));
    setItems(data);
  };
  
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const contentRef = useRef(null);

  const [currentItems, setCurrentItems] = useState(null);
  const [pageCount, setPageCount] = useState(0);
  // following the API or data you're working with.
  const [itemOffset, setItemOffset] = useState(0);

  useEffect(() => {
    // Fetch items from another resources.
    console.log(items);
    if (items == undefined) return;
    const endOffset = itemOffset + itemsPerPage;
    console.log(`Loading items from ${itemOffset} to ${endOffset}`);
    setCurrentItems(items.slice(itemOffset, endOffset));
    setPageCount(Math.ceil(items.length / itemsPerPage));
  }, [itemOffset, itemsPerPage, items]);

  // Invoke when user click to request another page.
  const handlePageClick = (event) => {
    const newOffset = (event.selected * itemsPerPage) % items.length;
    console.log(
      `User requested page number ${event.selected}, which is offset ${newOffset}`
    );
    setItemOffset(newOffset);
  };

  // console.log(`pagecount ${items.length}`);

  return (
    <main className={`search-page body`}>
      <Header className={`header`}>
          <Searchbar
            className={`searchbar`}
            handleSearch={handleSearch}
            time={time}
            show={true}
          />
      </Header>
      <div className={`content`} ref={contentRef}>
        <div className={`content_result`}>
          <div className={`scroll-wrapper`}>
            {/* <ArrowLeft />
            <ArrowRight /> */}
            {CreateSearchResult(currentItems, setSelectedIndex, selectedIndex)}
            <ReactPaginate
              nextLabel=">"
              onPageChange={handlePageClick}
              pageRangeDisplayed={3}
              marginPagesDisplayed={2}
              pageCount={pageCount}
              previousLabel="<"
              pageClassName="page-num"
              pageLinkClassName="page-link"
              previousClassName="page-item"
              previousLinkClassName="page-link-previous"
              nextClassName="page-item"
              nextLinkClassName="page-link-next"
              breakLabel="..."
              breakClassName="page-item"
              breakLinkClassName="page-link"
              containerClassName="pagination"
              activeClassName="active"
              pageNumber
              renderOnZeroPageCount={null}
            />
          </div>
        </div>
        <div className={`highlight`}></div>
      </div>
    </main>
  );
};

SearchPage.defaultProps = {
  itemsPerPage: 10,
};

export default SearchPage;
