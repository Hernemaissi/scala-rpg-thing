import React from "react"
import { useRecoilState } from "recoil"
import { startAdventure, travel } from "../../api/api"
import useRequest from "../../api/request"
import { Hero } from "../../models/Character"
import { setExistingPlayer } from "../../models/Player"
import { decodeQuest } from "../../models/Quest"
import { adventureState, playerState, selectedHeroState } from "../../state/atoms"

interface TownProps {
    hero: Hero
}

const Town = (props: TownProps) => {
    const { hero } = props
    const [_, setAdventure] = useRecoilState(adventureState)
    const [showAdventures, setShowAdventures] = React.useState(false)
    const onAdventureClicked = () => {
        setShowAdventures(!showAdventures)
    } 

    const onAdventureStart = async (adventureName: string) => {
        try {
            const adventure = await startAdventure(hero.name, adventureName)
            console.log("setting adventure")
            setAdventure(adventure)
        } catch (err) {
            console.log(err)
        }
    }

    return (
        <div>
            <div> You are standing in the town square. </div>
            <div>
                <TownActions {...props} onAdventureClicked={onAdventureClicked}  />
            </div>
            {showAdventures &&
                <div>
                    <button onClick={() => onAdventureStart("Grasslands")}> Grasslands </button>
                    <button onClick={() => setShowAdventures(false)}> Cancel </button>
                </div>
            }
        </div>
    )
}

const TownActions = ({ hero, onAdventureClicked }: TownProps & { onAdventureClicked: () => void }) => {
    const { requestMaker } = useRequest()
    const [_, setSelectedHero] = useRecoilState(selectedHeroState)
    const travelToShop = async () => {
        try {
            const travelResponse = await requestMaker(travel("Shop"))
            const quest = await decodeQuest(travelResponse)
            setSelectedHero({...hero, location: quest.location})
        } catch (err) {
            console.log(err)
        }
    }
    const travelToMageTower = async () => {
        try {
            const travelResponse = await requestMaker(travel("Mage Tower"))
            const quest = await decodeQuest(travelResponse)
            setSelectedHero({...hero, location: quest.location})
        } catch (err) {
            console.log(err)
        }
    }
    return (
        <div>
            <button onClick={travelToShop}> Shop </button>
            <button onClick={travelToMageTower}> Mage tower </button>
            <button onClick={onAdventureClicked}> Adventure </button>
        </div>
    )
}

export default Town