import { useState } from 'react'
import './App.css'
import LandingPage from './pages/LandingPage'
import ParticlesComponent from "./components/particles";


function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <ParticlesComponent id="particles" />
      <LandingPage />
    </>
  )
}

export default App
