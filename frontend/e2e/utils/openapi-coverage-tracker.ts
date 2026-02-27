import fs from 'fs';
import path from 'path';

export class OpenApiCoverageTracker {
    private static instance: OpenApiCoverageTracker;
    private spec: any = null;
    // Map of "METHOD /path" -> boolean (covered or not)
    private coverageMap: Record<string, boolean> = {};

    private constructor() { }

    public static getInstance(): OpenApiCoverageTracker {
        if (!OpenApiCoverageTracker.instance) {
            OpenApiCoverageTracker.instance = new OpenApiCoverageTracker();
        }
        return OpenApiCoverageTracker.instance;
    }

    /**
     * Loads the OpenAPI specification. Since our tests run against three separate services
     * (Core 8080, Auth 8081, Admin 8082), we'll fetch them from the locally running instances
     * at the beginning of the test suite and combine them, or just rely on a fetched JSON.
     */
    public async loadSpecs() {
        console.log('\n[OpenAPI Coverage] Loading API Specifications...');
        try {
            // In a real scenario we might fetch from three URLs and combine.
            // For simplicity in this demo, let's load core endpoints.
            const response = await fetch('http://localhost:8080/v3/api-docs');
            if (!response.ok) {
                throw new Error(`Failed to fetch Core API spec: ${response.statusText}`);
            }
            this.spec = await response.json();

            // Initialize all paths as false (uncovered)
            for (const [apiPath, methods] of Object.entries(this.spec.paths || {})) {
                for (const method of Object.keys(methods as any)) {
                    // Normalize to e.g., "GET /api/campaigns"
                    const key = `${method.toUpperCase()} ${apiPath}`;
                    this.coverageMap[key] = false;
                }
            }
            console.log(`[OpenAPI Coverage] Tracked ${Object.keys(this.coverageMap).length} endpoints.`);
        } catch (error) {
            console.warn('[OpenAPI Coverage] WARNING: Could not fetch API spec. Backend may not be running. Coverage will be skipped.', error);
        }
    }

    /**
     * Normalizes an actual request URL to the path templates defined in swagger.
     * e.g., "/api/campaigns/123" -> "/api/campaigns/{id}"
     */
    public registerRequest(method: string, url: string) {
        if (!this.spec || !this.spec.paths) return;

        try {
            const urlObj = new URL(url);
            const pathname = urlObj.pathname;

            if (!pathname.startsWith('/api/')) return;

            // Simple matching logic: find a path template that matches the url
            const templatePaths = Object.keys(this.spec.paths);
            let matchedTemplate = null;

            for (const template of templatePaths) {
                // Convert swagger /api/users/{id} to regex ^/api/users/[^/]+$
                const regexStr = '^' + template.replace(/\{[^}]+\}/g, '[^/]+') + '$';
                const regex = new RegExp(regexStr);
                if (regex.test(pathname)) {
                    matchedTemplate = template;
                    break;
                }
            }

            if (matchedTemplate) {
                const key = `${method.toUpperCase()} ${matchedTemplate}`;
                if (this.coverageMap[key] !== undefined) {
                    this.coverageMap[key] = true;
                }
            }
        } catch (e) {
            // invalid URL or cross-origin request we don't care about
        }
    }

    public reportCoverage() {
        if (!this.spec) {
            console.log('\n[OpenAPI Coverage] No spec loaded; skipping report.');
            return;
        }

        let coveredCount = 0;
        const totalCount = Object.keys(this.coverageMap).length;

        console.log('\n=============================================================');
        console.log('                 API CONTRACT COVERAGE                       ');
        console.log('=============================================================');

        for (const [endpoint, isCovered] of Object.entries(this.coverageMap)) {
            if (isCovered) {
                coveredCount++;
                console.log(`✅ COVERED : ${endpoint}`);
            } else {
                console.log(`❌ MISSED  : ${endpoint}`);
            }
        }

        const percentage = totalCount === 0 ? 0 : (coveredCount / totalCount) * 100;
        console.log('-------------------------------------------------------------');
        console.log(`Summary: ${coveredCount} / ${totalCount} endpoints covered (${percentage.toFixed(2)}%)`);
        console.log('=============================================================\n');

        // Make sure output dir exists
        const outputDir = path.join(process.cwd(), 'test-results');
        if (!fs.existsSync(outputDir)) {
            fs.mkdirSync(outputDir, { recursive: true });
        }

        fs.writeFileSync(
            path.join(outputDir, 'api-coverage.json'),
            JSON.stringify(
                {
                    total: totalCount,
                    covered: coveredCount,
                    percentage: percentage,
                    endpoints: this.coverageMap,
                },
                null,
                2
            )
        );
    }
}
