import { ReactComponent as D } from "../../assets/D.svg";
import { ReactComponent as E } from "../../assets/E.svg";
import { ReactComponent as I } from "../../assets/I.svg";
import { ReactComponent as L } from "../../assets/L.svg";
import { ReactComponent as P } from "../../assets/P.svg";
import { ReactComponent as R } from "../../assets/R.svg";
import { ReactComponent as S } from "../../assets/S.svg";
import { ReactComponent as Y } from "../../assets/Y.svg";
import "./Loader.css";

const Loader = () => {
  return (
    <>
      <div className="loader">
        <svg height="0" width="0" viewBox="0 0 64 64" className="absolute">
          <defs className="s-xJBuHA073rTt" xmlns="http://www.w3.org/2000/svg">
            <linearGradient
              className="s-xJBuHA073rTt"
              gradientUnits="userSpaceOnUse"
              y2="2"
              x2="0"
              y1="62"
              x1="0"
              id="c"
            >
              <stop
                className="s-xJBuHA073rTt"
                stopColor="#f25a02"
                offset="0"
              ></stop>
              <stop
                className="s-xJBuHA073rTt"
                stopColor="#f65b1e"
                offset="0.25"
              ></stop>
              <stop
                className="s-xJBuHA073rTt"
                stopColor="#fa5d2f"
                offset="0.5"
              ></stop>
              <stop
                className="s-xJBuHA073rTt"
                stopColor="#fd603d"
                offset="0.75"
              ></stop>
              <stop
                className="s-xJBuHA073rTt"
                stopColor="#ff634a"
                offset="1"
              ></stop>
            </linearGradient>
            <linearGradient
              className="s-xJBuHA073rTt"
              gradientUnits="userSpaceOnUse"
              y2="2"
              x2="0"
              y1="62"
              x1="0"
              id="bb"
            >
              <stop
                className="s-xJBuHA073rTt"
                stopColor="rgba(242,90,2,1)"
                offset="1"
              ></stop>
              <stop
                className="s-xJBuHA073rTt"
                stopColor="rgba(255,98,76,0.45)"
                offset="1"
              ></stop>
            </linearGradient>
            <linearGradient
              className="s-xJBuHA073rTt"
              gradientUnits="userSpaceOnUse"
              y2="0"
              x2="0"
              y1="64"
              x1="0"
              id="b"
            >
              <stop offset="0" stopColor="#D46435" stopOpacity="1" />
              <stop offset="1" stopColor="rgb(231, 82, 13)" stopOpacity="1" />

              <animateTransform
                repeatCount="indefinite"
                keySplines=".42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1;.42,0,.58,1"
                keyTimes="0; 0.125; 0.25; 0.375; 0.5; 0.625; 0.75; 0.875; 1"
                dur="25s"
                values="0 32 32;-270 32 32;-270 32 32;-540 32 32;-540 32 32;-810 32 32;-810 32 32;-1080 32 32;-1080 32 32"
                type="rotate"
                attributeName="gradientTransform"
              ></animateTransform>
            </linearGradient>
          </defs>
        </svg>
        <S />
        <P />
        <I />
        <D />
        <E />
        <R />
        <L />
        <Y />
      </div>
    </>
  );
};

export default Loader;
