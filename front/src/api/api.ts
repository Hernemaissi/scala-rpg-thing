import axios from "axios"
import { BattleActions, decodeAdventure, NextActions } from "../models/Adventure"
import { decodeCharacter, HeroClass } from "../models/Character"
import { PlayerLocation } from "../models/Location"

export const loadHeroes = () => 
    ({ method: "get", url: "http://localhost:9000/heroes" })

export const createHero = (name: string, heroClass: HeroClass, heroSlotId: number) => 
    ({ method: "post", url: "http://localhost:9000/create", data: {name, heroClass, heroSlotId} })

export const loadHero = (heroSlotId: number) =>
    ({ method: "post", url: "http://localhost:9000/load", data: { heroSlotId } })

export const startCharacter = (characterName: string, heroClass: HeroClass) => 
    ({ method: "post", url: "http://localhost:9000/start", data: { name: characterName, heroClass } })

export const travel = (location: PlayerLocation) => 
    ({ method: "post", url: "http://localhost:9000/travel", data: { location } })

export const getMerchandise = () =>
    ({ method: "get", url: "http://localhost:9000/shop/get" })

export const buyItem = (itemId: number) =>
    ({ method: "post", url: "http://localhost:9000/shop/buy", data: { itemId } })

export const useItem = (inventoryId: number) => 
    ({ method: "post", url: "http://localhost:9000/use", data: { inventoryId } })

export const startAdventure = async (characterName: string, adventureName: string) => {
    const res = await axios.post("http://localhost:9000/adventure/start", { name: characterName, adventureName})
    return decodeAdventure(res.data)
}

export const executeAttackAction = async (characterName: string, target: number) => {
    const res = await axios.post("http://localhost:9000/battle/action", {name: characterName, attackAction: { target }, spellAction: undefined})
    return decodeAdventure(res.data)
}

export const executeSpellAction = async (characterName: string, spellName: String, target: number | null) => {
    const res = await axios.post("http://localhost:9000/battle/action", {name: characterName, attackAction: undefined, spellAction: { spellName, target }})
    return decodeAdventure(res.data)
}

export const executeNextAction = async (characterName: string) => {
    const res = await axios.post("http://localhost:9000/adventure/next", { name: characterName, action: NextActions.FIGHT })
    return decodeAdventure(res.data)
}

export const executeLeaveAction = async (characterName: string) => {
    const res = await axios.post("http://localhost:9000/adventure/next", { name: characterName, action: NextActions.LEAVE })
    return decodeCharacter(res.data)
}

export const openTreasure = async (characterName: string) => {
    const res = await axios.post("http://localhost:9000/adventure/open", { name: characterName })
    return decodeCharacter(res.data)
}

export const addActiveSpell = (spellId: number) =>
    ({ method: "post", url: "http://localhost:9000/spell/active/add", data: { spellId } })

export const removeActiveSpell = (spellId: number) =>
    ({ method: "post", url: "http://localhost:9000/spell/active/remove", data: { spellId } })