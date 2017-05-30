import Region from '../inputkits/Location';

declare interface GoogleFitBridge {
    isGoogleApiClientConnected(): Promise<void>;
    requestPermissions(types: [string]): Promise<void>;
    getStepCount(date: number): Promise<{ value: number, startDate: number, endDate: number }>;
    startMonitoring(type: string): Promise<void>;
    stopMonitoring(type: string): Promise<void>;
}

declare interface AwarenessBridge {
    requestPermissions(): Promise<void>;
    startGeoFencing(callback: (region: Region) => {}):Promise<void>;
    stopGeoFencing(callback: (region: Region) => {}):Promise<void>;
}