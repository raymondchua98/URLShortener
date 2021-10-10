import { Route } from '../route-info';
import Router from '../router';
import { TransitionIntent } from '../transition-intent';
import TransitionState from '../transition-state';
export default class URLTransitionIntent<T extends Route> extends TransitionIntent<T> {
    preTransitionState?: TransitionState<T>;
    url: string;
    constructor(router: Router<T>, url: string, data?: {});
    applyToState(oldState: TransitionState<T>): TransitionState<T>;
}
