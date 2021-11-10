import React from "react"
import { AdventureState } from "../../models/Adventure"
import { Hero } from "../../models/Character"
import { makeSwitchExhaustive } from "../../utils"
import Battle from "./Battle"
import Treasure from "./Treasure"

interface AdventureProps {
    adventureState: AdventureState
    hero: Hero
}

const Adventure = ({ adventureState, hero }: AdventureProps) => {
    switch (adventureState.state) {
        case "Battle":
            return <Battle adventureBattle={adventureState} hero={hero} />
        case "Treasure":
            return <Treasure hero={hero} />
        default:
            return makeSwitchExhaustive(adventureState)
    }
}

export default Adventure