import { atom } from "recoil"
import { AdventureState } from "../models/Adventure"
import { Hero } from "../models/Character"
import { Player } from "../models/Player"

export const playerState = atom<Player>({
    key: "characterState",
    default: {
        kind: "none"
    }
})

export const selectedHeroState = atom<Hero | null>({
    key: "selectedHeroState",
    default: null
})

export const adventureState = atom<AdventureState | null>({
    key: "adventureState",
    default: null
})