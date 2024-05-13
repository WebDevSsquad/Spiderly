import { React, useRef, useState ,useEffect } from "react";
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
import "./SearchPage.css";
const items = [
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check these new released arts of Marwoona's (LEAKED).",
  },
  {
    iconPath: YTIcon,
    brand: "Youtube",
    protocol: "https://",
    link: "www.youtube.com",
    title: "Got diamond in THE FINALS!! - Marwoona",
    description:
      "Check this video about marwoona and her journey to achieve the diamond in THE FINALS.",
  },
  {
    iconPath: PinterestIcon,
    brand: "Pinterest",
    protocol: "https://",
    link: "www.pinterest.com",
    title: "Marwoona Portraits - 50 new ideas",
    description: "Check theseoona's (LEAKED).",
  },
];
const fetchContent = async (searchQuery) => {
  const response = await fetch(`http://localhost:8080/`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      searchQuery: searchQuery,
    }),
  });

  const res = await response.json();
};

const CreateSearchResult = (items) => {
  if(!items) return null;
  return items.map((item) => {
    return (
      <SearchResult
        iconPath={item.iconPath}
        brand={item.brand}
        protocol={item.protocol}
        link={item.link}
        title={item.title}
        description={item.description}
      />
    );
  });
};

const SearchPage = ({ itemsPerPage }) => {
  const contentRef = useRef(null);

  const [currentItems, setCurrentItems] = useState(null);
  const [pageCount, setPageCount] = useState(0);
  // Here we use item offsets; we could also use page offsets
  // following the API or data you're working with.
  const [itemOffset, setItemOffset] = useState(0);

  useEffect(() => {
    // Fetch items from another resources.
    const endOffset = itemOffset + itemsPerPage;
    console.log(`Loading items from ${itemOffset} to ${endOffset}`);
    setCurrentItems(items.slice(itemOffset, endOffset));
    setPageCount(Math.ceil(items.length / itemsPerPage));
  }, [itemOffset, itemsPerPage]);

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
        <Searchbar className={`searchbar`} />
      </Header>
      <div className={`content`} ref={contentRef}>
        <div className={`content_result`}>
          <div className={`scroll-wrapper`}>
            {/* <ArrowLeft />
            <ArrowRight /> */}
            {CreateSearchResult(currentItems)}
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

export default SearchPage;
