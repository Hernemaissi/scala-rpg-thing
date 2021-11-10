import React, { useState } from "react"
import { useRecoilState } from "recoil"
import { useItem as apiUseItem } from "../api/api"
import useRequest from "../api/request"
import { decodeHero, getGearName, Hero } from "../models/Character"
import { selectedHeroState } from "../state/atoms"

interface StatsProps {
    hero: Hero
}

const StatusScreen = ({ hero }: StatsProps) => {
    const { requestMaker } = useRequest()
    const [inventoryOpen, setInventoryOpen] = useState(false)
    const [_, setSelectedHero] = useRecoilState(selectedHeroState)
    const stats = hero.stats
    const gear = hero.gear

    const onUseItem = async (inventoryId: number) => {
        try {
            const useItemResp = await requestMaker(apiUseItem(inventoryId))
            const hero = await decodeHero(useItemResp)
            setSelectedHero(hero)
        } catch(err) {
            console.log(err)
        }
    }

    const inventoryComponent = () => {
        if (!inventoryOpen) {
            return <button onClick={() => setInventoryOpen(true)}> Inventory </button>
        }
        const inventoryItems = hero.inventory.items.map((islot, i) => {
            return (
                <div onClick={() => onUseItem(islot.inventoryId)}>
                    <p>{islot.item.name}</p>
                    <p>{islot.item.description} </p>
                </div>
            )
        })
        return (
            <div>
                <h3> Inventory </h3>
                {inventoryItems}
                <button onClick={() => setInventoryOpen(false)}> Close </button>
            </div>
        )
    }

    return (
        <div>
            <div>
                <h1> {hero.name} </h1>
                <div> Level: {hero.level} </div>
                <div> Exp: {hero.exp} </div>
                <br />
                <div> HP: {stats.maxHP} </div>
                <div> MP: {stats.maxMana} </div>
                <div> Strength: {stats.strength} </div>
                <div> Constitution: {stats.constitution} </div>
                <div> Intelligence: {stats.intelligence} </div>
                <div> Mind: {stats.mind} </div>
                <br />
                <div> Gold: {hero.gold} </div>
                <br />
                <h2> Gear </h2>
                <div> Weapon: {getGearName(gear.weapon, hero.inventory.items)}  </div>
                <div> Armor: {getGearName(gear.armor, hero.inventory.items)}  </div>
                <div> Accessory: {getGearName(gear.accessory, hero.inventory.items)}  </div>
            </div>
            {inventoryComponent()}
        </div>
    )
}

export default StatusScreen