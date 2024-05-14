import React, { useState } from "react";
import Header from "../components/Header/Header";
import Loader from "../components/Loader/Loader";
import Search from "../components/Search/Search";
import { SearchProvider } from "../utils/SearchContext";
import "./LandingPage.css";
import { useNavigate } from "react-router-dom";

import axios from "axios"; // Import the 'axios' package


const LandingPage = () => {
  const [startSearch, setStartSearch] = useState(false);
  const [items,setItems] = useState([]);
  const [time, setTime] = useState(0);
  const handleSearch = async (searchQuery,navigate) => {
    setStartSearch(true);
    try {
      const startTime = performance.now(); // Get start time
      const url = `http://localhost:8080/search?q=${searchQuery}`;
      const response = await axios.get(url, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      const endTime = performance.now(); // Get end time
      const timeInSeconds = (endTime - startTime) / 1000; // Calculate time taken in seconds

      // Update state with the fetched data and time taken
      setItems(response.data.documents);
      setTime(timeInSeconds.toFixed(2));
      console.log(response.data);
      navigate("/search")
    } catch (error) {
      console.error("Error fetching data:", error.response);
      // Handle the error appropriately
    }
    // const res = await response.json();
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
      <SearchProvider>
        <Search handleSearch={handleSearch} />
      </SearchProvider>
    </div>
  );
};

export default LandingPage;
