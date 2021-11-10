import React from "react"
import { useRecoilState } from "recoil"
import { buyItem, getMerchandise, travel } from "../../api/api"
import useRequest from "../../api/request"
import { decodeHero, Hero } from "../../models/Character"
import { PurchasableItem } from "../../models/Item"
import { setExistingPlayer } from "../../models/Player"
import { decodeQuest } from "../../models/Quest"
import { playerState, selectedHeroState } from "../../state/atoms"

interface ShopProps {
    hero: Hero
}

const Shop = ({ hero }: ShopProps) => {
    const { requestMaker } = useRequest()
    const [merchandise, setMerchandise] = React.useState<PurchasableItem[]>([])

    React.useEffect(() => {
        const loadMerchandise = async () => {
            try {
                const getMerchandiseResponse = await requestMaker(getMerchandise())
                setMerchandise(getMerchandiseResponse)
            } catch (err) {
                console.log(err)
            }
        }
        loadMerchandise()
    }, [])
    
    const shopList = merchandise.map(i => {
        return (
            <div key={i.id}>
                <p> {i.name} </p>
                <p> {i.description} </p>
                <p> Price: {i.price} </p>
                <button onClick={() => onBuyItem(i.id)} > Buy </button>
            </div>
        )
    })
    const [_, setSelectedHero] = useRecoilState(selectedHeroState)

    const onBuyItem = async (id: number) => {
        try {
            const buyResponse = await requestMaker(buyItem(id))
            const newHero = decodeHero(buyResponse)
            setSelectedHero(newHero)
        } catch (err) {
            console.log(err)
        }
    }

    const travelToTown = async () => {
        try {
            const travelResponse = await requestMaker(travel("Town"))
            const quest = await decodeQuest(travelResponse)
            setSelectedHero({...hero, location: quest.location})
        } catch (err) {
            console.log(err)
        }
    }

    return (
        <div>
            <h1> SHOP </h1>
            {merchandise.length === 0 && <div> Loading shop... </div>}
            {shopList}
            <button onClick={travelToTown}> Leave </button>
        </div>
    )
}

export default Shop