import React from "react"
import MessageLog from "../components/MessageLog";
import StatusScreen from "../components/StatusScreen";
import ViewPort from "../components/ViewPort";
import { Hero } from "../models/Character";

interface MainProps {
    hero: Hero
}

const Main = ({ hero }: MainProps) => {
    return (
        <div>
            <div className="main_screen">
                <div className="main_screen__view">
                    <ViewPort hero={hero} />
                </div>
                <div className="main_screen__status">
                    <StatusScreen
                        hero={hero}
                    />
                </div>
            </div>
            <div className="bottom_screen">
                <MessageLog />
            </div>
        </div>
    )
}

export default Main;