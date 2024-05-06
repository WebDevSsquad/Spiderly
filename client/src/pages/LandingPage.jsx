import React, { useEffect } from "react";
import "./LandingPage.css";
import Header from "../components/Header/Header";
import Search from "../components/Search/Search";
import Loader from "../components/Loader/Loader";

const LandingPage = () => {
  return (
    <div className="body">
      <Header />
      <div className="hero">
        <div className="hero_content">
          {/* <h1 className="hero__title montserrat-bold">
            <span className="gradient">Weave</span> Your Digital Web with{" "}
            <span className="gradient">Spiderly</span>
          </h1> */}
          {/* <h3 className="hero__subtitle montserrat-semibold">
            <span>Explore</span>
            <span>Discover</span>
            <span>Collect</span>
          </h3> */}
        </div>
      <Loader/>
      </div>
      <Search />
    </div> 
  );
};

export default LandingPage;
