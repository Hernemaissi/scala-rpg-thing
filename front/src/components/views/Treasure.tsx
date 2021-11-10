import React from "react"
import { useRecoilState } from "recoil"
import { openTreasure } from "../../api/api"
import { Hero } from "../../models/Character"
import { setExistingPlayer } from "../../models/Player"
import { adventureState, playerState } from "../../state/atoms"

interface TreasureProps {
    hero: Hero
}

const Treasure = ({ hero }: TreasureProps) => {
    const [loading, setLoading] = React.useState(false)
    const [_player, setPlayer] = useRecoilState(playerState)
    const [_adventure, setAdventure] = useRecoilState(adventureState)
    const onOpenTreasure = async () => {
        try {
            setLoading(true)
            const character = await openTreasure(hero.name)
            setLoading(false)
            setPlayer(setExistingPlayer(character))
            setAdventure(null)
        } catch (err) {
            console.log(err)
        }
    }

    return (
        <div>
            <h1> You found a treasure chest! Open it up to find what lies inside! </h1>
            <button disabled={loading} onClick={onOpenTreasure}> OPEN </button>
        </div>
    )
}

export default Treasure