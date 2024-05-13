import { useState } from 'react'
import './App.css'
import LandingPage from './pages/LandingPage'
import ParticlesComponent from "./components/particles";
import SearchPage from './pages/SearchPage';


function App() {
  const [count, setCount] = useState(0);

  return (
    <>
      <ParticlesComponent id="particles" />
      {/* <Router>
        <Switch>
        <Route path="/" exact component={LandingPage} />
        <Route path="/search" component={SearchPage} />
        <Redirect to="/" />
        </Switch>
      </Router> */}
      <SearchPage itemsPerPage={10} />
    </>
  )
}

export default App
