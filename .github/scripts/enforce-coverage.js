#!/usr/bin/env node
/*
Simple coverage enforcement script.
- Reads a JSON summary from stdin or a file (istanbul/nyc, jest --coverage, etc.)
- Enforces global >= 95%
- Enforces 95% for ALL individual files
- Enforces 100% for files matched as hot paths or error/security (by filename hints)
Exit non-zero on failure with a readable summary.
*/

const fs = require('fs');

function parseArgs() {
  const args = process.argv.slice(2);
  const params = { file: null, global: 95, perFile: 95, critical: 100 };
  for (let i = 0; i < args.length; i++) {
    if (args[i] === '--file' && args[i + 1]) {
      params.file = args[i + 1];
      i++;
    } else if (args[i] === '--global' && args[i + 1]) {
      params.global = parseFloat(args[i + 1]);
      i++;
    } else if (args[i] === '--per-file' && args[i + 1]) {
      params.perFile = parseFloat(args[i + 1]);
      i++;
    } else if (args[i] === '--critical' && args[i + 1]) {
      params.critical = parseFloat(args[i + 1]);
      i++;
    }
  }
  return params;
}

function loadSummary(file) {
  const input = file ? fs.readFileSync(file, 'utf8') : fs.readFileSync(0, 'utf8');
  return JSON.parse(input);
}

function isCritical(path) {
  const p = path.toLowerCase();
  return (
    p.includes('hot') ||
    p.includes('critical') ||
    p.includes('auth') ||
    p.includes('security') ||
    p.includes('error') ||
    p.includes('exception')
  );
}

function check(summary, thresholds) {
  const metrics = summary.total || summary;
  const globalLines = metrics.lines.pct || (metrics.lines.covered / metrics.lines.total * 100);
  const globalPass = globalLines >= thresholds.global;

  const failures = [];
  if (!globalPass) failures.push(`Global lines coverage ${globalLines.toFixed(2)}% < ${thresholds.global}%`);

  // Per-file checks if available
  if (summary && typeof summary === 'object') {
    for (const [file, m] of Object.entries(summary)) {
      if (file === 'total' || !m || !m.lines) continue;
      const filePct = m.lines.pct || (m.lines.covered / m.lines.total * 100);
      
      if (isCritical(file)) {
        if (filePct < thresholds.critical) {
          failures.push(`Critical path not fully covered: ${file} ${filePct.toFixed(2)}% < ${thresholds.critical}%`);
        }
      } else if (filePct < thresholds.perFile) {
        failures.push(`File below ${thresholds.perFile}%: ${file} ${filePct.toFixed(2)}%`);
      }
    }
  }

  return failures;
}

function main() {
  try {
    const thresholds = parseArgs();
    const summary = loadSummary(thresholds.file);
    const failures = check(summary, thresholds);
    if (failures.length) {
      console.error('Coverage enforcement failed:\n- ' + failures.join('\n- '));
      process.exit(1);
    }
    console.log('Coverage enforcement passed.');
  } catch (e) {
    console.error('Error running coverage enforcement:', e.message);
    process.exit(2);
  }
}

if (require.main === module) {
  main();
}

// © Capgemini 2025
