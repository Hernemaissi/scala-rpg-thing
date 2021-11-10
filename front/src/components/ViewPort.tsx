import React from "react"
import { useRecoilState } from "recoil"
import { Hero } from "../models/Character"
import { adventureState } from "../state/atoms"
import { makeSwitchExhaustive } from "../utils"
import Adventure from "./views/Adventure"
import MageTower from "./views/MageTower"
import Shop from "./views/Shop"
import Town from "./views/Town"

interface ViewPortProps {
    hero: Hero
}

const ViewPort = ({ hero }: ViewPortProps) => {
    const [adventure, _] = useRecoilState(adventureState)
    console.log("adventure is: ", adventure)
    if (adventure) {
        return <Adventure adventureState={adventure} hero={hero} />
    }
    const location = hero.location.name
        switch(location) {
            case "Town":
                return <Town hero={hero} />
            case "Shop":
                return <Shop hero={hero} />
            case "Mage Tower":
                return <MageTower hero={hero} />
            default:
                return makeSwitchExhaustive(location)
        }
}

export default ViewPort