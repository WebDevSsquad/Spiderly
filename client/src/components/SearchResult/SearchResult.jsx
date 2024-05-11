import React from 'react'
import SearchResultCSS from "./SearchResult.module.css"

const SearchResult = ({ iconPath, brand, protocol, link, title, description }) => {
  return (
    <div className={`${SearchResultCSS.card}`}>
      <div className={`${SearchResultCSS.info}`}>
        <div className={`${SearchResultCSS.logo}`}>
          <img className={`${SearchResultCSS.logo__icon}`} src={iconPath}></img>
          <div className={`${SearchResultCSS.logo__text} montserrat-bold`}>{brand}</div>
        </div>
        <div className={`${SearchResultCSS.link} montserrat-medium`}>{link}</div>
      </div>
      <a href={protocol + link} target='_blank noreferrer' className={`${SearchResultCSS.title} montserrat-semibold`}>{title}</a>
      <p className={`${SearchResultCSS.description} montserrat-regular`}>{description}</p>
    </div>
  )
}

export default SearchResult