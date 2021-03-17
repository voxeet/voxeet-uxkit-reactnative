import ConferenceStatusUpdatedEvent from "./events/ConferenceStatusUpdatedEvent";
interface EventMap {
    ["ConferenceStatusUpdatedEvent"]: ConferenceStatusUpdatedEvent;
}
export default class VoxeetEvents {
    private events;
    constructor();
    addListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
    removeListener<K extends keyof EventMap>(type: K, listener: (event: EventMap[K]) => void): void;
}
export {};
