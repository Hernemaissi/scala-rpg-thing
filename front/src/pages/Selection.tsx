import React, { useEffect, useState } from "react"
import { useRecoilState } from "recoil"
import { createHero, loadHero, loadHeroes } from "../api/api"
import { useRequest } from "../api/request"
import { decodeHero, decodeHeroSlots, HeroClass, HeroSlot, HeroSlots } from "../models/Character"
import { selectedHeroState } from "../state/atoms"
import { useAuth0 } from '@auth0/auth0-react';

const Selection = () => {
    const [name, setName] = useState("")
    const [heroSlots, setHeroSlots] = useState<HeroSlots | undefined>(undefined)
    const [selectedClass, setSelectedClass] = useState<HeroClass | undefined>(undefined)
    const [creationOpen, setCreationOpen] = useState(0)
    const [_, setSelectedHero] = useRecoilState(selectedHeroState)
    const { requestMaker } = useRequest()
    const { getAccessTokenWithPopup } = useAuth0()

    useEffect(() => {
        const fetchHeroes = async () => {
            try {
                const heroSlotResp = await requestMaker(loadHeroes())
                const heroSlotResponse = await decodeHeroSlots(heroSlotResp)
                setHeroSlots(heroSlotResponse)
            } catch (err) {
                console.log(err)
            }
        }
        fetchHeroes()
    }, [])

    const onHeroLoad = async (heroSlotId: number) => {
        try {
            const loadResp = await requestMaker(loadHero(heroSlotId))
            const loadedHero = await decodeHero(loadResp)
            setSelectedHero(loadedHero)
        } catch (err) {
            console.log(err)
        }
    }

    const onHeroCreate = async () => {
        if (name.length > 0 && !!selectedClass && creationOpen > 0) {
            try {
                const createResp = await requestMaker(createHero(name, selectedClass, creationOpen))
                const newHero = await decodeHero(createResp)
                setSelectedHero(newHero)
            } catch (err) {
                console.log(err)
            }
        }
    }

    if (!heroSlots) {
        return (
            
            <div><button onClick={() => { getAccessTokenWithPopup({audience: "https://adventure-test.example.com"}) }}>Consent to reading users</button></div>
        )
    }

    if (creationOpen) {
        return (
            <div>
                <input onChange={(ev) => setName(ev.target.value)} value={name} />
                <div>
                    <button onClick={() => setSelectedClass(HeroClass.Warrior)}> Warrior </button>
                    <button onClick={() => setSelectedClass(HeroClass.Wizard)}> Wizard </button>
                    <button onClick={() => setSelectedClass(HeroClass.Spellsword)}> Spellsword </button>
                </div>
                <div>
                    <button disabled={selectedClass === undefined || name.length <= 0} onClick={onHeroCreate}> Create </button>
                </div>
                <div>
                    <button onClick={() => setCreationOpen(0)}> Cancel </button>
                </div>
            </div>

        )
    }

    return (
        <div>
            {heroSlots.map(slot => {
                return (
                    <HeroSlotComponent key={slot.id} onCreateClicked={() => setCreationOpen(slot.id)} onContinueClicked={onHeroLoad} heroSlot={slot} />
                )
            })}
        </div>
    )
}

const HeroSlotComponent = ({heroSlot, onCreateClicked, onContinueClicked}: { heroSlot: HeroSlot, onCreateClicked: () => void, onContinueClicked: (heroSlotId
    : number) => void }) => {
    if (!heroSlot.hero) {
        return (
            <div>
                <p> Empty slot </p>
                <button onClick={onCreateClicked}> Create </button>
            </div>
        )
    }

    return (
        <div>
            <p> {heroSlot.hero.name} </p>
            <p> {heroSlot.hero.heroClass}</p>
            <p> Level: {heroSlot.hero.level}</p>
            <button onClick={() => onContinueClicked(heroSlot.id)}> Continue </button>
        </div>
    )
}

export default Selection;