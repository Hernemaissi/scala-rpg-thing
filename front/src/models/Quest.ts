import { pipe } from 'fp-ts/lib/function'
import * as t from 'io-ts'
import { fold } from 'fp-ts/Either'
import { decodeLeft, decodeRight } from "./decode-utils"

export const QuestValidator = t.type({
    location: t.type({
        name: t.union([t.literal("Town"), t.literal("Shop"), t.literal("Mage Tower")])
    })
})

export type Quest = t.TypeOf<typeof QuestValidator>

export const decodeQuest = (maybeQuest: any): Quest => {
    return pipe(QuestValidator.decode(maybeQuest), fold(
        decodeLeft,
        decodeRight
    ))
}