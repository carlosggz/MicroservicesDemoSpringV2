const fs = require('fs')
const process = require('process');
const path = require('path');
const { spawnSync } = require("child_process");
const os = require('os');

const scriptFolder = __dirname;
const rootFolder = path.join(scriptFolder, '..', '..');
const operatingSystem = os.platform().toLowerCase();
const gradleCmd = operatingSystem.startsWith('win') ? 'gradlew.bat' : './gradlew';

const executeCmd = (cmd, params) => {
    const result = spawnSync(cmd, params, {shell: false});

    if (result.error) {
        console.log(`Error executing command ${cmd}, message: ${result.error}`);
        console.log(`Current folder: ${process.cwd()}`);
        process.chdir(rootFolder);
        process.exit(1);
    }
}

const projects = [
    { name: 'Actors API', folder: 'actors-api', jar: 'actors-api-0.0.1-SNAPSHOT.jar' },
    { name: 'Movies API', folder: 'movies-api', jar: 'movies-api-0.0.1-SNAPSHOT.jar' },
    { name: 'API Gateway', folder: 'api-gateway', jar: 'api-gateway-0.0.1-SNAPSHOT.jar' },
    { name: 'Config Server', folder: 'config-server', jar: 'config-server-0.0.1-SNAPSHOT.jar' },
    { name: 'Eureka Server', folder: 'eureka-server', jar: 'eureka-server-0.0.1-SNAPSHOT.jar' },
];

console.log("Operating system: " + os.platform());
console.log("Gradle command: " + gradleCmd);

executeCmd('docker-compose', ['down']);

console.log('Removing old compilations...');

fs
    .readdirSync(scriptFolder)
    .filter(x => x.toLowerCase().endsWith('.jar'))
    .forEach(file => fs.unlinkSync(path.join(scriptFolder, file)));

process.chdir(rootFolder);

console.log("Compiling projects.....");

projects.forEach(p => {
    console.log(`${p.name}...`);
    process.chdir(p.folder);

    if (operatingSystem === 'linux') {
        executeCmd('chmod', ['+x', gradleCmd]);
    }

    executeCmd(gradleCmd, ['clean', 'assemble']);

    fs.copyFileSync(path.join(rootFolder, p.folder, 'build', 'libs', p.jar), path.join(scriptFolder, p.folder + '.jar'));

    process.chdir('..');
});

console.log("Compilation completed");
console.log("Now, execute the docker compose to start the execution:")
console.log('docker-compose --file ./docker-compose-all.yml up --build -d');

