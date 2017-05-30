// tslint:disable-next-line:no-reference
/// <reference path="../bridges/InputKit.d.ts" />

import { NativeModules, NativeEventEmitter } from 'react-native';
import EmitterEventListener from '../constants/EmitterEventListener';
import Region from './Location';

const AwarenessBridge = NativeModules.AwarenessBridge;
const awarenessBridgeEmitter = new NativeEventEmitter(AwarenessBridge);

class Awareness {

    private awarenessBridge: AwarenessBridge;
    private delegate: AwarenessDelegate;

    constructor() {
        this.awarenessBridge = AwarenessBridge;
    }

    setDelegate(delegate: AwarenessDelegate) {
        this.delegate = delegate;
    }

    requestPermissions() {
        console.log(this.awarenessBridge);
        return this.awarenessBridge.requestPermissions();
    }

    startGeofencing(type: AvailableType) {
        let monitoringType: string = '';
        let EMITTER_EVENT_NAME: string = '';
        if (type === 'geofence') {
            monitoringType = 'Geofencing';
            EMITTER_EVENT_NAME = EmitterEventListener.GEOFENCING_EVENT_LISTENER;
        }

        if (monitoringType !== '') {
            console.log('Start Monitoring : ' + monitoringType);
            awarenessBridgeEmitter.addListener(EMITTER_EVENT_NAME, (eventName, success) => {
                console.log(this);
                this.delegate.onAwarenessUpdated(eventName, success);
            });
            return this.awarenessBridge.startGeofencing(
                // tslint:disable-next-line:no-empty
                (region: Region) => {

                }
            );
        }
    }

    stopMonitoring(type: AvailableType) {
        let monitoringType: string = '';
        let EMITTER_EVENT_NAME: string = '';
        if (type === 'geofence') {
            monitoringType = 'Geofencing';
            EMITTER_EVENT_NAME = EmitterEventListener.GEOFENCING_EVENT_LISTENER;
        }

        if (monitoringType !== '') {
            console.log('Start Monitoring : ' + monitoringType);
            awarenessBridgeEmitter.removeListener(EMITTER_EVENT_NAME, () => {
                console.log(EMITTER_EVENT_NAME + ' removed.');
            });
            return this.awarenessBridge.stopGeoFencing(
                // tslint:disable-next-line:no-empty
                (region: Region) => {

                }
            );
        }
    }
}

export interface AwarenessDelegate {
    onAwarenessUpdated(eventName: string, success: boolean): void;
}

export type AvailableType = 'geofence';
let awareness: Awareness = null;

export default {
    reqSharedInstance: (): Promise<Awareness> => {
        if (awareness == null) {
            awareness = new Awareness();
            return Promise.resolve(awareness);
        }
        return Promise.resolve(awareness);
    }

};
