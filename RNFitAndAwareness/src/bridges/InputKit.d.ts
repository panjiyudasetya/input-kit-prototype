declare interface GoogleFitBridge {
    isHealthAvailable(): Promise<any>;
    requestPermissions(types: [string]): Promise<any>;
    getStepCount(startDate: number, endDate: number): Promise<any>;
    getStepCountDistribution(startDate: number, endDate: number, interval: string): Promise<any>;
}