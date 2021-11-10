import React from "react"
import { useRecoilState } from "recoil"
import { addActiveSpell, removeActiveSpell, travel } from "../../api/api"
import useRequest from "../../api/request"
import { decodeHero, Hero } from "../../models/Character"
import { decodeQuest } from "../../models/Quest"
import { selectedHeroState } from "../../state/atoms"

interface MageTowerProps {
    hero: Hero
}

const MageTower = ({ hero }: MageTowerProps) => {
    const { requestMaker } = useRequest()
    const [_, setSelectedHero] = useRecoilState(selectedHeroState)

    const travelToTown = async () => {
        try {
            const travelResponse = await requestMaker(travel("Town"))
            const quest = await decodeQuest(travelResponse)
            setSelectedHero({...hero, location: quest.location})
        } catch (err) {
            console.log(err)
        }
    }

    const onAddSpellToActive = async (spellId: number) => {
        try {
            const spellResponse = await requestMaker(addActiveSpell(spellId))
            const hero = await decodeHero(spellResponse)
            setSelectedHero(hero)
        } catch (err) {
            console.log(err)
        }
    }

    const onRemoveSpellFromActive = async (spellId: number) => {
        try {
            const spellResponse = await requestMaker(removeActiveSpell(spellId))
            const hero = await decodeHero(spellResponse)
            setSelectedHero(hero)
        } catch (err) {
            console.log(err)
        }
    }

    const canAddToActive = hero.activeSpells.length < hero.maxActiveSpells

    return (
        <div>
            <h1> Mage tower </h1>

            <h2> Known spells </h2>
            {hero.knownSpells.map(s => {
                return (
                    <button onClick={() => onAddSpellToActive(s.id)} disabled={!canAddToActive}> {s.name} </button>
                )
            })}

            <h2> Active spells </h2>
            {hero.activeSpells.map(s => {
                return (
                    <button onClick={() => onRemoveSpellFromActive(s.id)}> {s.name} </button>
                )
            })}
            <div>
              <button onClick={travelToTown}> Leave </button>
            </div>
        </div>
    )
}

export default MageTower