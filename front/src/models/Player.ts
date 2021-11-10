import { Character } from "./Character";

interface None {
    kind: "none"
}

interface New {
    kind: "new",
    name: string
}

export interface Existing {
    kind: "existing",
    character: Character
}

export type Player = None | New | Existing

export const setExistingPlayer = (character: Character | null): Existing | None => {
    
    return !!character 
    ? {
        kind: "existing",
        character
      }
    : {
        kind: "none"
    }
}

export const setNewPlayer = (name: string): New => {
    return {
        kind: "new",
        name
    }
}