# Use the official node image as the base image
FROM node:latest

# Set the working directory in the container
WORKDIR /app

# Copy the dockerpackage.json to the working directory and rename it to package.json
COPY dockerpackage.json ./package.json

# Install dependencies
RUN npm install

COPY webpackDockerPatch.config.js node_modules/react-scripts/config/webpackDevServer.config.js

# Copy the rest of the application code to the working directory
COPY . .

RUN npm run build

# Expose port 3000 (default port for Create React App) to the outside world
EXPOSE 3000

# Command to run the application
CMD ["npm", "start"]
