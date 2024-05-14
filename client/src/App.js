import { useState } from "react";
import {
  Redirect,
  Route,
  BrowserRouter as Router,
  Routes,
} from "react-router-dom";
import "./App.css";
import ParticlesComponent from "./components/particles";
import LandingPage from "./pages/LandingPage";
import SearchPage from "./pages/SearchPage";
import { SearchProvider } from "./utils/SearchContext";

function App() {
  return (
    <>
        <ParticlesComponent id="particles" />
        <SearchProvider>
          <Router>
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/search" element={<SearchPage />} />
            </Routes>
          </Router>
        </SearchProvider>
    </>
  );
}

export default App;
