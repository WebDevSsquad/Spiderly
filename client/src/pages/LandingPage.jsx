import React, { useState } from "react";
import Header from "../components/Header/Header";
import Loader from "../components/Loader/Loader";
import Search from "../components/Search/Search";
import "./LandingPage.css";

const LandingPage = () => {
  const [startSearch, setStartSearch] = useState(false);

  const handleSearch = (value) => {
    setStartSearch(value);
  };

  const heroContent = (
    <>
      <div className="hero_content">
        <h1 className="hero__title montserrat-bold">
          <span className="gradient">Weave</span> Your Digital Web with{" "}
          <span className="gradient">Spiderly</span>
        </h1>
        <h3 className="hero__subtitle montserrat-semibold">
          <span>Explore</span>
          <span>Discover</span>
          <span>Collect</span>
        </h3>
      </div>
    </>
  );
  return (
    <div className="body">
      <Header />
      <div className="hero">{!startSearch ? heroContent : <Loader />}</div>
      <Search handleSearch={handleSearch}  />
    </div>
  );
};

export default LandingPage;
