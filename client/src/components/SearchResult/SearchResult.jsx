import React, { useEffect, useRef, useState } from "react";
import {stemmer} from "stemmer";
import { RecentSearchItem } from "../Search/Search";
import SearchResultCSS from "./SearchResult.module.css";
const RelatedWord = ({ word }) => {
  return (
    <div className={`${SearchResultCSS.word} montserrat-medium`}>{word}</div>
  );
};

const handelAnimation = (
  cardRef,
  linkRef,
  linkSpanRef,
  titleRef,
  titleSpanRef,
  descriptionRef,
  wordsRef
) => {
  if (cardRef.current) {
    cardRef.current.animate(
      [
        { opacity: "0" }, // Initial styles
        { opacity: "1" }, // Final styles
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
  if (wordsRef.current) {
    wordsRef.current.animate(
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
  marwona,
  link,
  title,
  description,
  words,
  index,
  animate,
  setSelectedIndex,
}) => {
  const cardRef = useRef(null);
  const linkRef = useRef(null);
  const linkSpanRef = useRef(null);
  const titleRef = useRef(null);
  const titleSpanRef = useRef(null);
  const descriptionRef = useRef(null);
  const wordsRef = useRef(null);
  const [currIndex, setIndex] = useState(false);
  const [animation, setAnimation] = useState(false);
  useEffect(() => {
    setIndex(index);
    handelAnimation(
      cardRef,
      linkRef,
      linkSpanRef,
      titleRef,
      titleSpanRef,
      descriptionRef,
      wordsRef
    );
  }, []); // Empty dependency array ensures this effect runs only once

  let coloredDescription = (description)? description.split(" ").map((word, index) => {
    const lowerCaseWord = stemmer(word.toLowerCase());
    if (words.map(stemmer).includes(lowerCaseWord)) {
      return (
        <span key={index} className={`${SearchResultCSS.highlight}`}>
          {word + " "}
        </span>
      );
    }
    return word + " ";
  }):"";

  const handleClick = () => {
    setAnimation(true);
    setSelectedIndex(currIndex);
    if (cardRef) {
      cardRef.current.animate(
        [
          { transform: "translateX(0rem)", width: "100%" }, // Initial styles
          { transform: "translateX(1.5rem)", width: "110%" },
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
    if (linkRef) {
      linkRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
          { transform: "translateX(1.6rem)" },
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
          { transform: "translateX(2.5rem)" },
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
          { transform: "translateX(3.4rem)" },
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
    if (wordsRef) {
      wordsRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
          { transform: "translateX(4.3rem)" },
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
  const removeAnimation = () => {
    if (cardRef) {
      cardRef.current.animate(
        [
          { transform: "translateX(0rem)", width: "100%" }, // Initial styles
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
    if (linkRef) {
      linkRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
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
    if (wordsRef) {
      wordsRef.current.animate(
        [
          { transform: "translateX(0rem)" }, // Initial styles
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
  if (animate && animation) {
    removeAnimation();
    setAnimation(false);
  }
  return (
    <div
      onClick={!animation ? handleClick : () => {}}
      ref={cardRef}
      className={`${SearchResultCSS.card}`}
    >
      <div ref={linkRef} className={`${SearchResultCSS.link}`}>
        <span
          ref={linkSpanRef}
          className={`${SearchResultCSS.link_text} montserrat-medium`}
        >
          {link}
        </span>
      </div>
      <a
        ref={titleRef}
        href={link}
        target="_blank noreferrer"
        className={`${SearchResultCSS.title}`}
      >
        {/* <span ref={titleSpanRef}>{title}</span> */}
        <button className={`${SearchResultCSS.button}`} dataText="Awesome">
          <span
            className={`${SearchResultCSS.title__actual_text} montserrat-semibold`}
            ref={titleSpanRef}
          >
            &nbsp;{title}&nbsp;
          </span>
          <span
            ariaHidden="true"
            className={`${SearchResultCSS.hover_text} montserrat-semibold`}
          >
            &nbsp;{title}&nbsp;
          </span>
        </button>
      </a>
      <p
        ref={descriptionRef}
        className={`${SearchResultCSS.description} montserrat-regular`}
      >
        {coloredDescription}
      </p>
      {words && (
        <div
          ref={wordsRef}
          className={`${SearchResultCSS.words} montserrat-medium`}
        >
          {words.map((word) => (
            <RelatedWord word={word} />
          ))}
        </div>
      )}
    </div>
  );
};

export default SearchResult;
