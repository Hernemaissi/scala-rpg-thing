import * as t from 'io-ts'

export const decodeLeft = (errors: t.Errors) => {
    throw new Error(errors.map(error => error.context.map(({ key }) => key).join('.')).toString())
}

export const decodeRight = <T>(item: T): T => {
    return item
}