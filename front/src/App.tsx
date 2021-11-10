import React from "react"
import {
  RecoilRoot,
} from 'recoil';

import './App.css';
import Loader from "./Loader";

const App = () => {
  return (
    <RecoilRoot>
      <Loader />
    </RecoilRoot>
  )
}

export default App;
