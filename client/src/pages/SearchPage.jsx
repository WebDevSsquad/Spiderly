import React from 'react'
import Searchbar from '../components/Searchbar/Searchbar'
import "./SearchPage.css";
import Header from '../components/Header/Header';
import YTIcon from "../assets/.testing/YoutubeIcon.png"
import PinterestIcon from "../assets/.testing/PinterestIcon.png"
import GooglePlusIcon from "../assets/.testing/GooglePlusIcon.png"
import SearchResult from '../components/SearchResult/SearchResult';

const SearchPage = () => {
  return (
    <main className={`search-page body`}>
      <Header className={`header`}>
        <Searchbar className={`searchbar`} />
      </Header>
      <div className={`content`}>
        <div className={`content_result`}>
          <div className={`scroll-wrapper`}>
            <SearchResult 
              iconPath={YTIcon}
              brand={"Youtube"}
              protocol={"https://"}
              link={"www.youtube.com"}
              title={"Got diamond in THE FINALS!! - Marwoona"}
              description={"Check this video about marwoona and her journey to achieve the diamond in THE FINALS."}
            />
            <SearchResult 
              iconPath={PinterestIcon}
              brand={"Pinterest"}
              protocol={"https://"}
              link={"www.pinterest.com"}
              title={"Marwoona Portraits - 50 new ideas"}
              description={"Check these new released arts of Marwoona's (LEAKED)."}
            />
          </div>
        </div>
        <div className={`highlight`}>

        </div>
      </div>
    </main>
  )
}

export default SearchPage