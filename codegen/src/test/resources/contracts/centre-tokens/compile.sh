for f in *.sol; do
	docker run -v $PWD:/sources ethereum/solc:0.6.8 --abi --bin /sources/$f -o /sources/build/ @openzeppelin=/sources/openzeppelin --overwrite
done
