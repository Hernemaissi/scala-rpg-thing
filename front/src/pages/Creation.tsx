import React from "react"
import { useRecoilState } from "recoil"
import { startCharacter } from "../api/api"
import { decodeCharacter, HeroClass } from "../models/Character"
import { setExistingPlayer } from "../models/Player"
import { playerState } from "../state/atoms"
import useRequest from "../api/request"
import { useAuth0 } from '@auth0/auth0-react';

interface CreationProps {
    name: string
}

const Creation = ({ name }: CreationProps) => {
    const [validCharacter, setValidCharacter] = useRecoilState(playerState)
    const { requestMaker } = useRequest()
    const { getAccessTokenWithPopup } = useAuth0()
    const createCharacter = async (heroClass: HeroClass) => {
        try {
            const characterResp = await requestMaker(startCharacter(name, heroClass))
            const character = await decodeCharacter(characterResp)
            setValidCharacter(setExistingPlayer(character))
        } catch(err) {
            console.log(err)
        }
    }
    // Add the create character buttons
    return (
        <div>
            <h1> Choose your class </h1>
            <button onClick={() => { getAccessTokenWithPopup({audience: "https://adventure-test.example.com"}) }}>Consent to reading users</button>
            <button onClick={() => createCharacter(HeroClass.Warrior)}> Warrior </button>
            <button onClick={() => createCharacter(HeroClass.Wizard)}> Wizard </button>
            <button onClick={() => createCharacter(HeroClass.Spellsword)}> Spellsword </button>
        </div>
    )
}

export default Creation