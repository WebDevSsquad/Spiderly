import React, { useEffect, useRef, useState } from "react";
import SearchResultCSS from "./SearchResult.module.css";

const handelAnimation = (
  logoRef,
  iconRef,
  logoTextRef,
  linkRef,
  linkSpanRef,
  titleRef,
  titleSpanRef,
  descriptionRef
) => {
  if (logoRef.current) {
    logoRef.current.animate(
      [
        { opacity: "0", width: "0rem" }, // Initial styles
        { opacity: "1", width: "10rem" }, // Final styles
      ],
      {
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }

  if (iconRef.current) {
    iconRef.current.animate(
      [
        { opacity: "0" }, // Initial styles
        { opacity: "1" }, // Final styles
      ],
      {
        delay: 500, // Wait for 0.5 seconds before starting
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }

  if (logoTextRef.current) {
    logoTextRef.current.animate(
      [
        { opacity: "0" }, // Initial styles
        { opacity: "1" }, // Final styles
      ],
      {
        delay: 500, // Wait for 0.5 seconds before starting
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }

  if (linkRef.current && linkSpanRef.current) {
    const parentPadding =
      parseFloat(getComputedStyle(linkRef.current).paddingLeft) +
      parseFloat(getComputedStyle(linkRef.current).paddingRight);
    linkRef.current.animate(
      [
        { opacity: "0", width: "0rem" }, // Initial styles
        {
          opacity: "1",
          width: `${linkSpanRef.current.offsetWidth + parentPadding}px`,
        }, // Final styles
      ],
      {
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }
  if (titleRef.current && titleSpanRef.current) {
    const parentPadding =
      parseFloat(getComputedStyle(titleRef.current).paddingLeft) +
      parseFloat(getComputedStyle(titleRef.current).paddingRight);
    titleRef.current.animate(
      [
        { opacity: "0", width: "0rem" }, // Initial styles
        {
          opacity: "1",
          width: `${titleSpanRef.current.offsetWidth + parentPadding}px`,
        }, // Final styles
      ],
      {
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }
  if (descriptionRef.current) {
    descriptionRef.current.animate(
      [
        { opacity: "0", transform: "translateX(-10rem)" }, // Initial styles
        { opacity: "1", transform: "translateX(0rem)" },
      ],
      {
        duration: 1000, // 1 second
        iterations: 1,
        direction: "normal", // Run animation forwards
        easing: "ease-in-out", // Easing function
        fill: "forwards", // Retain the final state after the animation
      }
    );
  }
};

const SearchResult = ({
  contentRef,
  iconPath,
  brand,
  protocol,
  link,
  title,
  description,
}) => {
  const logoRef = useRef(null);
  const iconRef = useRef(null);
  const logoTextRef = useRef(null);
  const linkRef = useRef(null);
  const linkSpanRef = useRef(null);
  const titleRef = useRef(null);
  const titleSpanRef = useRef(null);
  const descriptionRef = useRef(null);
  useEffect(() => {
    handelAnimation(
      logoRef,
      iconRef,
      logoTextRef,
      linkRef,
      linkSpanRef,
      titleRef,
      titleSpanRef,
      descriptionRef
    );
  }, []); // Empty dependency array ensures this effect runs only once

  const handleClick = () => {
    if (logoRef.current) {
      logoRef.current.animate(
        [
          { transform: "scale(1) translateX(0rem)" }, // Initial styles
          { transform: "scale(1.2) translateX(2rem)" },
        ],
        {
          duration: 1000, // 1 second
          iterations: 1,
          direction: "normal", // Run animation forwards
          easing: "ease-in-out", // Easing function
          fill: "forwards", // Retain the final state after the animation
        }
      );
    }
    if (linkRef) {
      linkRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
          { transform: "translateX(5rem)" },
        ],
        {
          delay: 500, // Wait for 0.5 seconds before starting
          duration: 1000, // 1 second
          iterations: 1,
          direction: "normal", // Run animation forwards
          easing: "ease", // Easing function
          fill: "forwards", // Retain the final state after the animation
        }
      );
    }
    if (titleRef) {
      titleRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
          { transform: "translateX(7rem)" },
        ],
        {
          delay: 500, // Wait for 0.5 seconds before starting
          duration: 1000, // 1 second
          iterations: 1,
          direction: "normal", // Run animation forwards
          easing: "ease", // Easing function
          fill: "forwards", // Retain the final state after the animation
        }
      );
    }
    if (descriptionRef) {
      descriptionRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
          { transform: "translateX(9rem)" },
        ],
        {
          delay: 500, // Wait for 0.5 seconds before starting
          duration: 1000, // 1 second
          iterations: 1,
          direction: "normal", // Run animation forwards
          easing: "ease", // Easing function
          fill: "forwards", // Retain the final state after the animation
        }
      );
    }
  };

  return (
    <div className={`${SearchResultCSS.card}`}>
      <div className={`${SearchResultCSS.info}`}>
        <div className={`${SearchResultCSS.logo} `} ref={logoRef}>
          <img
            className={`${SearchResultCSS.logo__icon}`}
            ref={iconRef}
            src={iconPath}
          ></img>
          <div
            ref={logoTextRef}
            className={`${SearchResultCSS.logo__text} montserrat-bold`}
          >
            {brand}
          </div>
        </div>
        <div
          ref={linkRef}
          className={`${SearchResultCSS.link} montserrat-medium`}
        >
          <span ref={linkSpanRef}>{link}</span>
        </div>
      </div>
      <a
        ref={titleRef}
        href={protocol + link}
        target="_blank noreferrer"
        className={`${SearchResultCSS.title} montserrat-semibold`}
      >
        {/* <span ref={titleSpanRef}>{title}</span> */}
        <button className={`${SearchResultCSS.button}`} dataText="Awesome">
          <span className="actual-text" ref={titleSpanRef}>
            &nbsp;{title}&nbsp;
          </span>
          <span ariaHidden="true" className={`${SearchResultCSS.hover_text}`}>
            &nbsp;{title}&nbsp;
          </span>
        </button>
      </a>
      <p
        onClick={handleClick}
        ref={descriptionRef}
        className={`${SearchResultCSS.description} montserrat-regular`}
      >
        {description}
      </p>
    </div>
  );
};

export default SearchResult;
