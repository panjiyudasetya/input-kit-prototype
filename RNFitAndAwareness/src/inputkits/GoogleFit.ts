// tslint:disable-next-line:no-reference
/// <reference path="../bridges/InputKit.d.ts" />

import { NativeModules, NativeEventEmitter } from 'react-native';
import EmitterEventListener from '../constants/EmitterEventListener';

const GoogleFitBridge = NativeModules.GoogleFitBridge;
const googleFitBridgeEmitter = new NativeEventEmitter(GoogleFitBridge);

class GoogleFit {

    private googleFitBridge: GoogleFitBridge;
    private delegate: GoogleFitDelegate;

    constructor() {
        this.googleFitBridge = GoogleFitBridge;
    }

    setDelegate(delegate: GoogleFitDelegate) {
        this.delegate = delegate;
    }

    requestPermissions(types: [AvailableType]) {
        console.log(this.googleFitBridge);
        console.log(types);
        return this.googleFitBridge.requestPermissions(types);
    }

    getStepCount(date: Date) {
        return this.googleFitBridge.getStepCount(date.getTime());
    }

    startMonitoring(type: AvailableType) {
        let monitoringType: string = '';
        let EMITTER_EVENT_NAME: string = '';
        if (type === 'stepsCount') {
            monitoringType = 'Steps Count';
            EMITTER_EVENT_NAME = EmitterEventListener.STEPS_COUNT_EVENT_LISTENER;
        }

        if (monitoringType !== '') {
            console.log('Start Monitoring : ' + monitoringType);
            googleFitBridgeEmitter.addListener(EMITTER_EVENT_NAME, (eventName, success) => {
                console.log(this);
                this.delegate.onGoogleFitUpdates(eventName, success);
            });
            return this.googleFitBridge.startMonitoring(type);
        }
    }

    stopMonitoring(type: AvailableType) {
        let monitoringType: string = '';
        let EMITTER_EVENT_NAME: string = '';
        if (type === 'stepsCount') {
            monitoringType = 'Steps Count';
            EMITTER_EVENT_NAME = EmitterEventListener.STEPS_COUNT_EVENT_LISTENER;
        }

        if (monitoringType !== '') {
            console.log('Start Monitoring : ' + monitoringType);
            googleFitBridgeEmitter.removeListener(EMITTER_EVENT_NAME, () => {
                console.log(EMITTER_EVENT_NAME + ' removed.');
            });
            return this.googleFitBridge.stopMonitoring(type);
        }
    }
}

export interface GoogleFitDelegate {
    onGoogleFitUpdates(eventName: string, success: boolean): void;
}

export type AvailableType = 'stepsCount';
let googleFit: GoogleFit = null;

export default {
    reqSharedInstance: (): Promise<GoogleFit> => {
        if (googleFit == null) {
            googleFit = new GoogleFit();
            return Promise.resolve(googleFit);
        }
        return Promise.resolve(googleFit);
    }

};
