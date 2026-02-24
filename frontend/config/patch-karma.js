const fs = require('node:fs');
const path = require('node:path');

const karmaPath = path.join(__dirname, '..', 'node_modules', 'karma', 'lib', 'file-list.js');

if (fs.existsSync(karmaPath)) {
    let content = fs.readFileSync(karmaPath, 'utf8');
    if (content.includes("const mm = require('minimatch')")) {
        console.log('Patching Karma file-list.js for minimatch 10 compatibility...');
        content = content.replace(
            "const mm = require('minimatch')",
            "const mm_imported = require('minimatch')\nconst mm = typeof mm_imported === 'function' ? mm_imported : mm_imported.minimatch"
        );
        fs.writeFileSync(karmaPath, content, 'utf8');
        console.log('Patch applied successfully.');
    } else if (content.includes("const mm_imported = require('minimatch')")) {
        console.log('Karma is already patched.');
    } else {
        console.warn('Could not find expected minimatch import in Karma file-list.js. Patching skipped.');
    }
} else {
    console.warn('Karma file-list.js not found. Patching skipped.');
}
