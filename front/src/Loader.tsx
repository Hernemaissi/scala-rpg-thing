import React, { useEffect, useState } from 'react';
import { useAuth0 } from "@auth0/auth0-react";

import Main from './pages/Main';
import Selection from './pages/Selection';
import Login from "./pages/Login"

import { useRecoilState } from 'recoil';
import { selectedHeroState } from './state/atoms';

const Loader = () => {
  const { isAuthenticated, isLoading } = useAuth0();
  const [selectedHero, _] = useRecoilState(selectedHeroState)

  if (isLoading) {
    return <div> Loading... </div>
  }

  if (!isAuthenticated) {
    return <Login />
  } 

  if (!selectedHero) {
    return <Selection />
  }

  return <Main hero={selectedHero} />
}

export default Loader;
