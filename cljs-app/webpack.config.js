module.exports = {
  mode: "development",
  devServer: {
    static: ["./target/public", "./resources/public"],
    compress: true,
    port: 9000,
  },
  experiments: {
    asyncWebAssembly: true,
  },
};
